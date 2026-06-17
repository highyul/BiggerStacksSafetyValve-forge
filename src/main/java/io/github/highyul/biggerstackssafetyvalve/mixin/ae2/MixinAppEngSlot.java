package io.github.highyul.biggerstackssafetyvalve.mixin.ae2;

import appeng.menu.AEBaseMenu;
import appeng.menu.slot.AppEngSlot;
import io.github.highyul.biggerstackssafetyvalve.ducks.ae2.IAE2SlotAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AppEngSlot.class)
public abstract class MixinAppEngSlot
        implements IAE2SlotAccessor {

    @Shadow(remap = false)
    protected abstract AEBaseMenu getMenu();

    @Override
    public AEBaseMenu biggerstackssafetyvalve$getMenu() {
        return this.getMenu();
    }
}