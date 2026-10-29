package io.github.highyul.biggerstackssafetyvalve.compat.sophisticatedcore;

import io.github.highyul.biggerstackssafetyvalve.BiggerStacksSafetyValve;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class RecipeTransferRemover {

    //Upper bound on Slot#remove calls per ingredient group, in case a slot hands out items in tiny chunks.
    private static final int MAX_REMOVE_CALLS = 4096;

    private RecipeTransferRemover() {}

    private static final class Group {
        final ItemStack template;
        int consumers;
        long available;
        ItemStack sample;
        final List<Slot> sources = new ArrayList<>();

        Group(ItemStack template) {
            this.template = template;
        }
    }

    public static Map<Integer, ItemStack> removeItems(
            Player player,
            StorageContainerMenuBase<?> container,
            Map<Integer, ItemStack> required,
            List<Integer> craftingSlots,
            List<Integer> inventorySlots,
            boolean maxTransfer) {

        Map<Integer, ItemStack> result = new HashMap<>(required.size());


        List<Group> groups = new ArrayList<>();
        Map<Integer, Group> groupByRecipeSlot = new HashMap<>();
        int stackCap = Integer.MAX_VALUE;

        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            ItemStack req = entry.getValue();
            if (req.isEmpty()) {
                continue;
            }
            Group group = findGroup(groups, req);
            if (group == null) {
                group = new Group(req.copy());
                groups.add(group);
            }
            group.consumers++;
            groupByRecipeSlot.put(entry.getKey(), group);
            stackCap = Math.min(stackCap, Math.max(1, req.getMaxStackSize()));
        }
        if (groups.isEmpty()) {
            return result;
        }


        Set<Integer> indices = new LinkedHashSet<>(craftingSlots.size() + inventorySlots.size());
        indices.addAll(craftingSlots);
        indices.addAll(inventorySlots);

        int totalSlots = container.getTotalSlotsNumber();
        for (int index : indices) {
            if (index < 0 || index >= totalSlots) {
                continue;
            }
            Slot slot = container.getSlot(index);
            if (!slot.mayPickup(player)) {
                continue;
            }
            ItemStack inSlot = slot.getItem();
            if (inSlot.isEmpty()) {
                continue;
            }
            Group group = findGroup(groups, inSlot);
            if (group == null) {
                continue;
            }
            if (group.sample == null) {
                group.sample = inSlot.copy();
            }
            group.sources.add(slot);
            group.available += inSlot.getCount();
        }


        long sets = maxTransfer ? stackCap : 1L;
        for (Group group : groups) {
            sets = Math.min(sets, group.available / group.consumers);
        }
        if (sets <= 0) {
            return result;
        }


        Map<Slot, ItemStack> originals = new HashMap<>();
        for (Group group : groups) {
            long need = sets * group.consumers;

            for (Slot slot : group.sources) {
                if (need <= 0) {
                    break;
                }
                if (slot.getItem().isEmpty()) {
                    continue;
                }
                originals.putIfAbsent(slot, slot.getItem().copy());

                int calls = 0;
                while (need > 0 && calls++ < MAX_REMOVE_CALLS) {
                    ItemStack current = slot.getItem();
                    if (current.isEmpty()) {
                        break;
                    }
                    ItemStack removed = slot.remove((int) Math.min(need, current.getCount()));
                    if (removed.isEmpty()) {
                        break;
                    }
                    need -= removed.getCount();
                }
            }

            if (need > 0) {
                BiggerStacksSafetyValve.LOGGER.warn(
                        "Recipe transfer could not remove all required items (missing {}); rolling back.", need);
                for (Map.Entry<Slot, ItemStack> original : originals.entrySet()) {
                    original.getKey().set(original.getValue());
                }
                return new HashMap<>();
            }
        }


        for (Map.Entry<Integer, Group> entry : groupByRecipeSlot.entrySet()) {
            result.put(entry.getKey(), entry.getValue().sample.copyWithCount((int) sets));
        }
        return result;
    }

    private static Group findGroup(List<Group> groups, ItemStack stack) {
        for (Group group : groups) {
            if (ItemStack.isSameItemSameTags(group.template, stack)) {
                return group;
            }
        }
        return null;
    }
}
