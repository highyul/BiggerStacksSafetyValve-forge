package io.github.highyul.biggerstackssafetyvalve.mixin.compat.sophisticatedcore;

import io.github.highyul.biggerstackssafetyvalve.compat.sophisticatedcore.RecipeTransferRemover;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.CraftingContainerRecipeTransferHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = CraftingContainerRecipeTransferHandlerServer.class, remap = false)
public class MixinCraftingContainerRecipeTransferHandlerServer {

    @Inject(
            method = "removeItemsFromInventory",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void biggerstackssafetyvalve$removeItemsFast(
            Player player,
            StorageContainerMenuBase<?> container,
            Map<Integer, ItemStack> required,
            List<Integer> craftingSlots,
            List<Integer> inventorySlots,
            boolean maxTransfer,
            CallbackInfoReturnable<Map<Integer, ItemStack>> cir) {

        cir.setReturnValue(RecipeTransferRemover.removeItems(
                player, container, required, craftingSlots, inventorySlots, maxTransfer));
    }
}
