package io.github.highyul.biggerstackssafetyvalve.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.github.highyul.biggerstackssafetyvalve.BiggerStacksSafetyValve;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class RuntimeConfig {

    private static final String KEY = "shiftClickCraftLimit";

    private static final String COMMENT = """
            Maximum number of crafting operations performed by a single shift-click.
            Valid range: %d - %d (out-of-range values are clamped, invalid values are ignored).

            Example:
            If this value is set to 10 and you shift-click a stack of 64 logs,
            only 10 crafting operations will be performed,
            producing 40 planks (10 crafts x 4 planks per log).

            Helps prevent freezes and crashes when using extremely large stack sizes.

            """.formatted(RuntimeConfigData.MIN_CRAFTING_LIMIT, RuntimeConfigData.MAX_CRAFTING_LIMIT);


    private static final Object LOCK = new Object();

    private static volatile RuntimeConfigData current = new RuntimeConfigData();

    private static volatile Path configPath;

    private RuntimeConfig() {}

    public static int getCraftingLimits() {
        return current.craftingLimits;
    }


    public static boolean load(Path path) {
        synchronized (LOCK) {
            configPath = path;

            try {
                if (!Files.exists(path)) {
                    persist(path, current.craftingLimits);
                    return true;
                }

                try (CommentedFileConfig config = open(path)) {
                    config.load();


                    if (config.isEmpty()) {
                        return false;
                    }

                    Object raw = config.get(KEY);
                    if (raw == null) {
                        BiggerStacksSafetyValve.LOGGER.warn(
                                "Runtime config has no '{}' entry; keeping the current value ({}).",
                                KEY, current.craftingLimits);
                        return true;
                    }

                    ConfigValueParser.Parsed parsed = ConfigValueParser.parseCraftingLimit(raw);
                    if (!parsed.valid()) {
                        BiggerStacksSafetyValve.LOGGER.warn(
                                "Invalid value '{}' for '{}' (expected an integer in {}..{}); keeping the current value ({}).",
                                raw, KEY, RuntimeConfigData.MIN_CRAFTING_LIMIT,
                                RuntimeConfigData.MAX_CRAFTING_LIMIT, current.craftingLimits);
                        return true;
                    }
                    if (parsed.clamped()) {
                        BiggerStacksSafetyValve.LOGGER.warn(
                                "Value '{}' for '{}' is out of range {}..{}; using {} instead.",
                                raw, KEY, RuntimeConfigData.MIN_CRAFTING_LIMIT,
                                RuntimeConfigData.MAX_CRAFTING_LIMIT, parsed.value());
                    }

                    RuntimeConfigData data = new RuntimeConfigData();
                    data.craftingLimits = parsed.value();
                    current = data;
                    return true;
                }
            } catch (Exception e) {
                BiggerStacksSafetyValve.LOGGER.warn("Failed to load runtime config", e);
                return false;
            }
        }
    }

    public record UpdateResult(int applied, boolean persisted) {}


    public static UpdateResult updateCraftingLimits(int requested) {
        synchronized (LOCK) {
            int applied = ConfigValueParser.clamp(requested);

            RuntimeConfigData data = new RuntimeConfigData();
            data.craftingLimits = applied;
            current = data;

            Path path = configPath;
            if (path == null) {
                BiggerStacksSafetyValve.LOGGER.error("Config path is not initialized yet; value applied in memory only.");
                return new UpdateResult(applied, false);
            }

            try {
                persist(path, applied);
                return new UpdateResult(applied, true);
            } catch (Exception e) {
                BiggerStacksSafetyValve.LOGGER.error("Failed to save runtime config; value applied in memory only.", e);
                return new UpdateResult(applied, false);
            }
        }
    }

    private static CommentedFileConfig open(Path path) {
        // No autosave: the only writes are the explicit persist() calls below.
        return CommentedFileConfig.builder(path)
                .sync()
                .build();
    }


    private static void persist(Path path, int value) throws IOException {
        Path abs = path.toAbsolutePath();
        Path parent = abs.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path tmp = abs.resolveSibling(abs.getFileName() + ".tmp");
        try {
            try (CommentedFileConfig config = open(tmp)) {
                config.setComment(KEY, COMMENT);
                config.set(KEY, value);
                config.save();
            }
            try {
                Files.move(tmp, abs, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, abs, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
