package io.github.highyul.biggerstackssafetyvalve.mixin.jei;


import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import mezz.jei.common.transfer.BasicRecipeTransferHandlerServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Map;



@Mixin(value = BasicRecipeTransferHandlerServer.class, remap = false)
public class MixinBasicRecipeTransferHandlerServer {


    @Unique
    private static final ThreadLocal<CraftLoopCounter>
            biggerstackssafetyvalve$counter =
            ThreadLocal.withInitial(CraftLoopCounter::new);


    @Inject(method = "takeItemsFromInventory", at = @At("HEAD"))
    private static void onTakeItemsFromInventoryHead(Player player, Map<?, ?> recipeSlotToRequiredItemStack, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean transferAsCompleteSets, boolean maxTransfer, CallbackInfoReturnable<Map<Slot, ItemStack>> cir) {
        biggerstackssafetyvalve$counter.get().reset();
    }


    @Inject(
            method = "removeOneSetOfItemsFromInventory",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onRemoveOneSetHead(Player player, Map<?, ?> recipeSlotToRequiredItemStack, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean transferAsCompleteSets, CallbackInfoReturnable<Map<Slot, ItemStack>> cir) {



        if (!biggerstackssafetyvalve$counter.get().tryIncrement()) {
            cir.setReturnValue(Collections.emptyMap());
        }

    }
}
