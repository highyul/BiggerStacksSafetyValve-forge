package io.github.highyul.biggerstackssafetyvalve.util;

public class CraftLoopCounter {

    private int count;
    private long lastTick = -1L;

    public boolean tryIncrementForTick(long currentTick) {
        if (currentTick != lastTick) {
            count = 0;
            lastTick = currentTick;
        }
        if (count >= CraftLimitHelper.getLimit()) {
            return false;
        }
        count++;
        return true;
    }
}