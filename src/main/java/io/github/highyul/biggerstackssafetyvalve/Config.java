package io.github.highyul.biggerstackssafetyvalve;

import io.github.highyul.biggerstackssafetyvalve.compat.CompatMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MAX_CRAFT_COUNT;


    private static final Map<String, ForgeConfigSpec.BooleanValue> MOD_MIXIN_SWITCHES = new HashMap<>();


    private static final Map<String, Boolean> EARLY_CACHE = new HashMap<>();
    private static boolean isEarlyLoaded = false;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");
        MAX_CRAFT_COUNT = builder
                .comment("Maximum number of crafts allowed per shift-click operation.")
                .defineInRange("maxCraftCount", 8192, 4, Integer.MAX_VALUE);
        builder.pop();


        builder.push("mixin_compatibility");
        builder.comment("Force disable mixins for specific mods if they cause compatibility issues.");

        for (CompatMod mod : CompatMod.values()) {
            ForgeConfigSpec.BooleanValue configValue = builder
                    .comment("Enable mixins for " + mod.id)
                    .define("enable_" + mod.id, true);
            MOD_MIXIN_SWITCHES.put(mod.id, configValue);
        }
        builder.pop();

        SPEC = builder.build();
    }


    public static boolean isModMixinEnabled(String modId) {
        if (!isEarlyLoaded) {
            ForgeConfigSpec.BooleanValue value = MOD_MIXIN_SWITCHES.get(modId);
            return value == null || value.get();
        }
        return EARLY_CACHE.getOrDefault(modId, true);
    }


    public static void loadEarly() {
        if (isEarlyLoaded) return;


        Path configPath = FMLPaths.CONFIGDIR.get().resolve("biggerstackssafetyvalve-common.toml");

        try {
            CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            configData.load();


            for (String modId : MOD_MIXIN_SWITCHES.keySet()) {
                boolean enabled = configData.getOrElse("mixin_compatibility.enable_" + modId, true);
                EARLY_CACHE.put(modId, enabled);
            }

            configData.close();
        } catch (Exception e) {
            for (String modId : MOD_MIXIN_SWITCHES.keySet()) {
                EARLY_CACHE.put(modId, true);
            }
        }

        isEarlyLoaded = true;
    }
}