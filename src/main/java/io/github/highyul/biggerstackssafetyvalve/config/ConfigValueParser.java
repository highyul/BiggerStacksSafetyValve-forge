package io.github.highyul.biggerstackssafetyvalve.config;

import java.math.BigDecimal;
import java.math.BigInteger;


public final class ConfigValueParser {


    public record Parsed(boolean valid, int value, boolean clamped) {
        static final Parsed INVALID = new Parsed(false, 0, false);
    }

    private static final BigDecimal MIN = BigDecimal.valueOf(RuntimeConfigData.MIN_CRAFTING_LIMIT);
    private static final BigDecimal MAX = BigDecimal.valueOf(RuntimeConfigData.MAX_CRAFTING_LIMIT);

    private ConfigValueParser() {}

    public static int clamp(long value) {
        return (int) Math.max(RuntimeConfigData.MIN_CRAFTING_LIMIT,
                Math.min(RuntimeConfigData.MAX_CRAFTING_LIMIT, value));
    }

    public static Parsed parseCraftingLimit(Object raw) {
        if (!(raw instanceof Number n)) {
            return Parsed.INVALID;
        }

        BigDecimal d;
        try {
            if (n instanceof Double || n instanceof Float) {
                double dv = n.doubleValue();
                if (Double.isNaN(dv) || Double.isInfinite(dv)) {
                    return Parsed.INVALID;
                }
                d = BigDecimal.valueOf(dv);
            } else if (n instanceof BigDecimal bd) {
                d = bd;
            } else if (n instanceof BigInteger bi) {
                d = new BigDecimal(bi);
            } else {
                d = BigDecimal.valueOf(n.longValue());
            }
        } catch (NumberFormatException e) {
            return Parsed.INVALID;
        }


        if (d.signum() != 0 && d.stripTrailingZeros().scale() > 0) {
            return Parsed.INVALID;
        }

        if (d.compareTo(MIN) < 0) {
            return new Parsed(true, RuntimeConfigData.MIN_CRAFTING_LIMIT, true);
        }
        if (d.compareTo(MAX) > 0) {
            return new Parsed(true, RuntimeConfigData.MAX_CRAFTING_LIMIT, true);
        }
        return new Parsed(true, d.intValueExact(), false);
    }
}
