package io.github.highyul.biggerstackssafetyvalve.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.github.highyul.biggerstackssafetyvalve.BiggerStacksSafetyValve;

import java.nio.file.Files;
import java.nio.file.Path;

public final class RuntimeConfig {

    private static volatile RuntimeConfigData current = new RuntimeConfigData();

    private static Path configPath;

    private RuntimeConfig() {}

    public static int getCraftingLimits() {
        return current.craftingLimits;
    }


    public static void load(Path path) {
        configPath = path;

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());

                CommentedFileConfig config = CommentedFileConfig.builder(path)
                        .sync()
                        .autosave()
                        .build();

                config.load();

                config.setComment(
                        "shiftClickCraftLimit",
                        """
                                Maximum number of crafting operations performed by a single shift-click.
                                
                                Example:
                                If this value is set to 10 and you shift-click a stack of 64 logs,
                                only 10 crafting operations will be performed,
                                producing 40 planks (10 crafts x 4 planks per log).
                                
                                Helps prevent freezes and crashes when using extremely large stack sizes.
                                
                                """
                );


                config.set("shiftClickCraftLimit", current.craftingLimits);
                config.save();
                config.close();
            }

            CommentedFileConfig config = CommentedFileConfig.builder(path)
                    .sync()
                    .autosave()
                    .build();

            config.load();

            RuntimeConfigData data = new RuntimeConfigData();


            data.craftingLimits = config.getOrElse(
                    "shiftClickCraftLimit",
                    data.craftingLimits
            );

            current = data;
            config.close();

        } catch (Exception e) {
            BiggerStacksSafetyValve.LOGGER.error(
                    "Failed to load runtime config",
                    e
            );
        }
    }


    public static void updateCraftingLimits(int newValue) {
        if (configPath == null) {
            BiggerStacksSafetyValve.LOGGER.error("Config path is not initialized yet.");
            return;
        }

        try {

            RuntimeConfigData data = new RuntimeConfigData();
            data.craftingLimits = newValue;
            current = data;


            try (CommentedFileConfig config = CommentedFileConfig.builder(configPath).sync().build()) {
                config.load();
                config.set("shiftClickCraftLimit", newValue);
                config.save();
            }

        } catch (Exception e) {
            BiggerStacksSafetyValve.LOGGER.error(
                    "Failed to update and save runtime config",
                    e
            );
        }
    }
}