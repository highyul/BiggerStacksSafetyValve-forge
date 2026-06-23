package io.github.highyul.biggerstackssafetyvalve.config;

import io.github.highyul.biggerstackssafetyvalve.BiggerStacksSafetyValve;

import java.nio.file.*;

public final class RuntimeConfigWatcher {

    private final Path configFile;

    public RuntimeConfigWatcher(Path configFile) {
        this.configFile = configFile;
    }

    public void start() {

        Thread thread = new Thread(() -> {

            try (WatchService watchService =
                         FileSystems.getDefault().newWatchService()) {

                configFile.getParent().register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_MODIFY
                );

                while (!Thread.currentThread().isInterrupted()) {

                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {

                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        Path changed = (Path) event.context();

                        if (changed.equals(configFile.getFileName())) {

                            BiggerStacksSafetyValve.LOGGER.info(
                                    "Runtime config changed, reloading..."
                            );

                            RuntimeConfig.load(configFile);
                        }
                    }

                    if (!key.reset()) {
                        break;
                    }
                }

            } catch (InterruptedException ignored) {

                Thread.currentThread().interrupt();

            } catch (Exception e) {

                BiggerStacksSafetyValve.LOGGER.error(
                        "Runtime config watcher stopped unexpectedly",
                        e
                );
            }

        }, "BiggerStacksSafetyValve-RuntimeConfigWatcher");

        thread.setDaemon(true);
        thread.start();
    }
}