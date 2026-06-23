package io.github.highyul.biggerstackssafetyvalve.util;

import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfig;

public class CraftLoopCounter {

    private int count;
    private long lastTick = -1L;

    public boolean tryIncrementForTick(long currentTick) {
        if (currentTick != lastTick) {
            count = 0;
            lastTick = currentTick;
        }

        int craftingLimits = RuntimeConfig.getCraftingLimits();
        if (count >= craftingLimits) {
            return false;
        }
        count++;
        return true;
    }
}