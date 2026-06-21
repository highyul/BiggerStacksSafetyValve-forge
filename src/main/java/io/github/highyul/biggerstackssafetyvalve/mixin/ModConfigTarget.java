package io.github.highyul.biggerstackssafetyvalve.mixin;

import io.github.highyul.biggerstackssafetyvalve.Config;
import io.github.highyul.biggerstackssafetyvalve.compat.CompatMod;
import java.util.function.BooleanSupplier;
import net.minecraftforge.fml.loading.LoadingModList;

public enum ModConfigTarget {
    VANILLA(null, () -> !isActive(CompatMod.FAST_BENCH)),
    FAST_BENCH(CompatMod.FAST_BENCH, () -> isActive(CompatMod.FAST_BENCH)),
    AE2(CompatMod.AE2, () -> isActive(CompatMod.AE2)),
    JEI(CompatMod.JEI, () -> isActive(CompatMod.JEI)),
    REFINED_STORAGE(CompatMod.REFINED_STORAGE, () -> isActive(CompatMod.REFINED_STORAGE)),
    TOMS_STORAGE(CompatMod.TOMS_STORAGE, () -> isActive(CompatMod.TOMS_STORAGE));

    private final CompatMod mod;
    private final BooleanSupplier condition;

    ModConfigTarget(CompatMod mod, BooleanSupplier condition) {
        this.mod = mod;
        this.condition = condition;
    }

    public boolean matches(String mixinClassName) {
        if (this == VANILLA) {
            return mixinClassName.contains(".vanilla.");
        }
        return mixinClassName.contains(".compat." + mod.id + ".");
    }

    public boolean shouldApply() {
        return condition.getAsBoolean();
    }

    private static boolean isActive(CompatMod mod) {
        return isLoaded(mod.id) && Config.isModMixinEnabled(mod.id);
    }

    private static boolean isLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}