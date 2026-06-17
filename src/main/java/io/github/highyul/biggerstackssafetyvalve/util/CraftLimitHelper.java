package io.github.highyul.biggerstackssafetyvalve.util;

import io.github.highyul.biggerstackssafetyvalve.Config;

public final class CraftLimitHelper {

    private CraftLimitHelper() {}

    public static int getLimit() {
        return Config.MAX_CRAFT_COUNT.get();
    }
}