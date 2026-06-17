package io.github.highyul.biggerstackssafetyvalve;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MAX_CRAFT_COUNT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");

        MAX_CRAFT_COUNT = builder
                .comment("Maximum number of crafts allowed per shift-click operation.")
                .defineInRange("maxCraftCount", 8192, 4, Integer.MAX_VALUE);

        builder.pop();

        SPEC = builder.build();
    }
}