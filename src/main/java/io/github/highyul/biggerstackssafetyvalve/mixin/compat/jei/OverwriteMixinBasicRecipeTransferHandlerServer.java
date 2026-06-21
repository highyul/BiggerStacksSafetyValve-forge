package io.github.highyul.biggerstackssafetyvalve.mixin.compat.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.highyul.biggerstackssafetyvalve.util.jei.TransferHelper;
import mezz.jei.common.transfer.BasicRecipeTransferHandlerServer;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.common.transfer.TransferOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BasicRecipeTransferHandlerServer.class, remap = false)
public abstract class OverwriteMixinBasicRecipeTransferHandlerServer {

    @Unique
    private static final Logger biggerstackssafetyvalve$LOGGER = LogManager.getLogger("JEIFastTransfer");

    @Shadow
    private static List<ItemStack> clearCraftingGrid(List<Slot> craftingSlots, Player player) {
        throw new AssertionError();
    }

    @Shadow
    private static List<ItemStack> putItemsIntoCraftingGrid(Map<Slot, ItemStack> recipeSlotToTakenStacks, boolean requireCompleteSets) {
        throw new AssertionError();
    }

    @Shadow
    private static void stowItems(Player player, List<Slot> inventorySlots, List<ItemStack> itemStacks) {
        throw new AssertionError();
    }



    /**
     * @author highyul
     * @reason Optimize recipe transfer to O(1) arithmetic formulas to prevent loops and server freezing.
     */
    @Overwrite
    public static void setItems(Player player, List<TransferOperation> transferOperations, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
        if (RecipeTransferUtil.validateSlots(player, transferOperations, craftingSlots, inventorySlots)) {
            Map<Slot, ItemStack> recipeSlotToTakenStacks = biggerstackssafetyvalve$takeRequiredItemsFast(player, transferOperations, craftingSlots, inventorySlots, maxTransfer);

            if (!recipeSlotToTakenStacks.isEmpty()) {
                List<ItemStack> clearedCraftingItems = clearCraftingGrid(craftingSlots, player);
                List<ItemStack> remainderItems = putItemsIntoCraftingGrid(recipeSlotToTakenStacks, requireCompleteSets);
                stowItems(player, inventorySlots, clearedCraftingItems);
                stowItems(player, inventorySlots, remainderItems);
                AbstractContainerMenu container = player.containerMenu;
                container.broadcastChanges();
            } else {
                biggerstackssafetyvalve$LOGGER.error("Tried to transfer recipe but was unable to remove any items from the inventory.");
            }
        }
    }

    @Unique
    private static Map<Slot, ItemStack> biggerstackssafetyvalve$takeRequiredItemsFast(Player player, List<TransferOperation> transferOperations, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean maxTransfer) {
        List<TransferHelper.Need> needs = new ArrayList<>(transferOperations.size());
        for (TransferOperation op : transferOperations) {
            Slot craftingSlot = op.craftingSlot(player.containerMenu);
            Slot inventorySlot = op.inventorySlot(player.containerMenu);
            if (!inventorySlot.allowModification(player)) {
                biggerstackssafetyvalve$LOGGER.error("Tried to transfer recipe but was given an inventory slot that the player can't pickup from: {}", inventorySlot.index);
                return Map.of();
            }
            ItemStack slotStack = inventorySlot.getItem();
            if (slotStack.isEmpty()) {
                biggerstackssafetyvalve$LOGGER.error("Tried to transfer recipe but was given an empty inventory slot as an ingredient source: {}", inventorySlot.index);
                return Map.of();
            }
            ItemStack required = slotStack.copy();
            required.setCount(1);
            needs.add(new TransferHelper.Need(craftingSlot, required));
        }
        if (needs.isEmpty()) {
            return Map.of();
        }

        List<TransferHelper.Group> groups = new ArrayList<>();
        for (TransferHelper.Need need : needs) {
            TransferHelper.Group target = null;
            for (TransferHelper.Group g : groups) {
                if (ItemStack.isSameItemSameTags(g.template, need.item())) {
                    target = g;
                    break;
                }
            }
            if (target == null) {
                target = new TransferHelper.Group(need.item());
                groups.add(target);
            }
            target.consumers.add(need);
        }

        List<Slot> searchOrder = new ArrayList<>(craftingSlots.size() + inventorySlots.size());
        searchOrder.addAll(craftingSlots);
        searchOrder.addAll(inventorySlots);
        for (Slot slot : searchOrder) {
            if (!slot.allowModification(player)) {
                continue;
            }
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            for (TransferHelper.Group g : groups) {
                if (ItemStack.isSameItemSameTags(g.template, stack)) {
                    g.sources.add(new TransferHelper.Source(slot, stack.getCount()));
                    g.available += stack.getCount();
                    break;
                }
            }
        }

        int maxSets = maxTransfer ? Integer.MAX_VALUE : 1;
        for (TransferHelper.Group g : groups) {
            int consumedPerSet = g.consumers.size();
            int setsForThisGroup = g.available / consumedPerSet;
            maxSets = Math.min(maxSets, setsForThisGroup);
        }
        if (maxSets <= 0) {
            return Map.of();
        }

        Map<Slot, ItemStack> result = new HashMap<>();
        for (TransferHelper.Group g : groups) {
            int remainingToTake = g.consumers.size() * maxSets;
            for (TransferHelper.Source src : g.sources) {
                if (remainingToTake <= 0) {
                    break;
                }
                int take = Math.min(remainingToTake, src.amount());
                src.slot().safeTake(take, take, player);
                remainingToTake -= take;
            }
            for (TransferHelper.Need consumer : g.consumers) {
                ItemStack taken = g.template.copy();
                taken.setCount(maxSets);
                result.put(consumer.craftingSlot(), taken);
            }
        }
        return result;
    }

}