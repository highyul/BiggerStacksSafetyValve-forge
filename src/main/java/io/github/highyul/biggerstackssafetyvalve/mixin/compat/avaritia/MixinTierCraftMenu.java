package io.github.highyul.biggerstackssafetyvalve.mixin.compat.avaritia;

import committee.nova.mods.avaritia.common.menu.TierCraftMenu;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("resource")
@Mixin(TierCraftMenu.class)
public abstract class MixinTierCraftMenu {

    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

    @Inject(
            method = "quickMoveStack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void biggerstackssafetyvalve$onQuickMoveStackHead(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        // Slot 0 is the result slot.
        if (slotIndex != 0) return;
        if (!this.biggerstackssafetyvalve$craftCounter.tryIncrementForTick(player.level().getGameTime())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
