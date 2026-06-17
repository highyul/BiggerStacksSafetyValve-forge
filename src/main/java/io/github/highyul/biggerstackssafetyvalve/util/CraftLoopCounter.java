package io.github.highyul.biggerstackssafetyvalve.util;

public class CraftLoopCounter {

    private int count;

    public void reset() {
        count = 0;
    }


    public boolean tryIncrement() {
        if (count >= CraftLimitHelper.getLimit()) {
            return false;
        }

        count++;
        return true;
    }
}