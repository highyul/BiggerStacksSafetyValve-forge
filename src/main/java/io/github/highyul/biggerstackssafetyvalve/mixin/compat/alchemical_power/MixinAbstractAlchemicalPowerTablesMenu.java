package io.github.highyul.biggerstackssafetyvalve.mixin.compat.alchemical_power;

import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import jp.nogami_rion.alchemical_power.screen.AbstractAlchemicalPowerTablesMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Common base menu of the Alchemy Table, Hermes Workbench and Transcendental Table
 * (the "_re" blocks). The legacy tables do not extend this class and are not affected.
 */
@SuppressWarnings("resource")
@Mixin(AbstractAlchemicalPowerTablesMenu.class)
public abstract class MixinAbstractAlchemicalPowerTablesMenu {

    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

    @Inject(
            method = "quickMoveStack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void biggerstackssafetyvalve$onQuickMoveStackHead(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (slotIndex < 0 || slotIndex >= menu.slots.size()) return;
        // Only the result slot is limited (its container is a ResultContainer).
        if (!(menu.slots.get(slotIndex).container instanceof ResultContainer)) return;
        if (!this.biggerstackssafetyvalve$craftCounter.tryIncrementForTick(player.level().getGameTime())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
