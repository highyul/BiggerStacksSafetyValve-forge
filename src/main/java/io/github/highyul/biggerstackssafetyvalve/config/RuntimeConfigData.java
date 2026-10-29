package io.github.highyul.biggerstackssafetyvalve.config;

public class RuntimeConfigData {

    public static final int DEFAULT_CRAFTING_LIMIT = 8192;
    public static final int MIN_CRAFTING_LIMIT = 1;
    public static final int MAX_CRAFTING_LIMIT = Integer.MAX_VALUE;

    public int craftingLimits = DEFAULT_CRAFTING_LIMIT;
}
