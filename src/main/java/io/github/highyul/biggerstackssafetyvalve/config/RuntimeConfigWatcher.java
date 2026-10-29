package io.github.highyul.biggerstackssafetyvalve.config;

import io.github.highyul.biggerstackssafetyvalve.BiggerStacksSafetyValve;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class RuntimeConfigWatcher {


    private static final long DEBOUNCE_MS = 300L;

    private static final long MAX_WAIT_MS = 3000L;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 250L;

    private final Path configFile;
    private final BooleanSupplier reloader;
    private final long debounceMs;

    private Thread thread;

    public RuntimeConfigWatcher(Path configFile) {
        this(configFile, () -> RuntimeConfig.load(configFile), DEBOUNCE_MS);
    }

    RuntimeConfigWatcher(Path configFile, BooleanSupplier reloader, long debounceMs) {
        this.configFile = configFile.toAbsolutePath();
        this.reloader = reloader;
        this.debounceMs = debounceMs;
    }

    public synchronized void start() {
        if (thread != null) {
            return;
        }
        Thread t = new Thread(this::run, "BiggerStacksSafetyValve-RuntimeConfigWatcher");
        t.setDaemon(true);
        thread = t;
        t.start();
    }

    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private void run() {
        Path dir = configFile.getParent();
        Path fileName = configFile.getFileName();

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            // CREATE is needed as well: atomic saves (temp file + rename) surface as CREATE, not MODIFY.
            dir.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            boolean dirty = false;
            long firstEventAt = 0L;
            long lastEventAt = 0L;

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key;
                if (!dirty) {
                    key = watchService.take();
                } else {
                    long now = System.nanoTime();
                    long quietLeft = debounceMs - TimeUnit.NANOSECONDS.toMillis(now - lastEventAt);
                    long maxLeft = MAX_WAIT_MS - TimeUnit.NANOSECONDS.toMillis(now - firstEventAt);
                    long wait = Math.min(quietLeft, maxLeft);
                    key = wait > 0 ? watchService.poll(wait, TimeUnit.MILLISECONDS) : null;
                }

                if (key == null) {
                    // quiet period elapsed (or max wait reached)
                    dirty = false;
                    reload();
                    continue;
                }

                boolean relevant = false;
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        relevant = true; // events may have been lost; reload to be safe
                    } else if (event.context() instanceof Path changed && changed.equals(fileName)) {
                        relevant = true;
                    }
                }

                if (relevant) {
                    long now = System.nanoTime();
                    if (!dirty) {
                        dirty = true;
                        firstEventAt = now;
                    }
                    lastEventAt = now;
                }

                if (!key.reset()) {
                    BiggerStacksSafetyValve.LOGGER.warn(
                            "Runtime config directory is no longer watchable; hot reload disabled.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            BiggerStacksSafetyValve.LOGGER.error("Runtime config watcher stopped unexpectedly", e);
        }
    }

    private void reload() throws InterruptedException {
        BiggerStacksSafetyValve.LOGGER.info("Runtime config changed, reloading...");

        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (reloader.getAsBoolean()) {
                    return;
                }
            } catch (RuntimeException e) {
                BiggerStacksSafetyValve.LOGGER.warn("Runtime config reload attempt {} failed", attempt + 1, e);
            }
            if (attempt < MAX_RETRIES) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        BiggerStacksSafetyValve.LOGGER.warn("Runtime config could not be reloaded; keeping the previous value.");
    }
}
