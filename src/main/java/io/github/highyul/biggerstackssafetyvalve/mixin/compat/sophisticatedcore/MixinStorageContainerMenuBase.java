package io.github.highyul.biggerstackssafetyvalve.mixin.compat.sophisticatedcore;

import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StorageContainerMenuBase.class, remap = false)
public class MixinStorageContainerMenuBase {

    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

    @Inject(
            method = "quickMoveStack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onQuickMoveStack(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.biggerstackssafetyvalve$craftCounter.tryIncrementForTick(player.level().getGameTime())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}