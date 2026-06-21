package io.github.highyul.biggerstackssafetyvalve.mixin.compat.toms_storage;

import com.tom.storagemod.gui.CraftingTerminalMenu;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("resource")
@Mixin(value = CraftingTerminalMenu.class, remap = false)
public class MixinCraftingTerminalMenu {

    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();




    @Inject(
            method = "shiftClickItems(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onShiftClickItemsHead(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (player.level().isClientSide) return;
        if (!biggerstackssafetyvalve$craftCounter.tryIncrementForTick(player.level().getGameTime())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}