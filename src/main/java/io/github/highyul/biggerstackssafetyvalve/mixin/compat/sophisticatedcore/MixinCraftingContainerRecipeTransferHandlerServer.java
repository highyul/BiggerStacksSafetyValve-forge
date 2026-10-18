package io.github.highyul.biggerstackssafetyvalve.mixin.compat.sophisticatedcore;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.CraftingContainerRecipeTransferHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = CraftingContainerRecipeTransferHandlerServer.class, remap = false)
public class MixinCraftingContainerRecipeTransferHandlerServer {

    @Inject(
            method = "removeItemsFromInventory",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void optimizedRemoveItemsFromInventory(
            Player player,
            StorageContainerMenuBase<?> container,
            Map<Integer, ItemStack> required,
            List<Integer> craftingSlots,
            List<Integer> inventorySlots,
            boolean maxTransfer,
            CallbackInfoReturnable<Map<Integer, ItemStack>> cir) {

        Map<Integer, ItemStack> result = new HashMap<>(required.size());


        List<Integer> targetSlotIndices = new ArrayList<>(craftingSlots.size() + inventorySlots.size());
        targetSlotIndices.addAll(craftingSlots);
        targetSlotIndices.addAll(inventorySlots);

        boolean itemAddedInLoop;

        do {
            itemAddedInLoop = false;
            Map<Slot, ItemStack> cycleOriginalSlotContents = new HashMap<>();
            Map<Integer, ItemStack> cycleFoundItems = new HashMap<>(required.size());


            for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
                int recipeSlotId = entry.getKey();
                ItemStack req = entry.getValue();
                if (req.isEmpty()) continue;

                ItemStack currentResult = result.get(recipeSlotId);

                if (currentResult != null && currentResult.getCount() >= currentResult.getMaxStackSize()) {
                    continue;
                }

                Slot foundSlot = null;

                for (int slotIdx : targetSlotIndices) {
                    if (slotIdx < 0 || slotIdx >= container.slots.size()) continue;
                    Slot slot = container.getSlot(slotIdx);

                    if (!slot.mayPickup(player)) continue;
                    ItemStack stackInSlot = slot.getItem();

                    if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameTags(stackInSlot, req)) {
                        foundSlot = slot;
                        break;
                    }
                }

                if (foundSlot == null) {
                    for (Map.Entry<Slot, ItemStack> rollbackEntry : cycleOriginalSlotContents.entrySet()) {
                        rollbackEntry.getKey().set(rollbackEntry.getValue());
                    }
                    cir.setReturnValue(result);
                    return;
                }


                if (!cycleOriginalSlotContents.containsKey(foundSlot)) {
                    cycleOriginalSlotContents.put(foundSlot, foundSlot.getItem().copy());
                }


                ItemStack removed = foundSlot.remove(1);
                cycleFoundItems.put(recipeSlotId, removed);
            }


            for (Map.Entry<Integer, ItemStack> entry : cycleFoundItems.entrySet()) {
                int slotId = entry.getKey();
                ItemStack stack = entry.getValue();

                ItemStack existing = result.get(slotId);
                if (existing == null) {
                    result.put(slotId, stack.copy());
                } else {
                    existing.grow(stack.getCount());
                }
                itemAddedInLoop = true;
            }

        } while (maxTransfer && itemAddedInLoop);

        cir.setReturnValue(result);
    }
}