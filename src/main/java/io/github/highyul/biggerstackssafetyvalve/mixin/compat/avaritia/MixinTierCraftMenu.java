package io.github.highyul.biggerstackssafetyvalve.mixin.compat.avaritia;

import committee.nova.mods.avaritia.common.menu.TierCraftMenu;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TierCraftMenu.class)
public abstract class MixinTierCraftMenu {


    @Shadow private Level world;


    @Unique
    private final CraftLoopCounter custom$loopCounter = new CraftLoopCounter();


    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void onQuickMoveStackHead(Player player, int slotNumber, CallbackInfoReturnable<ItemStack> cir) {

        if (slotNumber == 0) {

            long currentTick = this.world.getGameTime();

            if (!this.custom$loopCounter.tryIncrementForTick(currentTick)) {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }
}