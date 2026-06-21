package io.github.highyul.biggerstackssafetyvalve.util.jei;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class TransferHelper {

    public record Need(Slot craftingSlot, ItemStack item) {
    }

    public record Source(Slot slot, int amount) {
    }

    public static final class Group {
        public final ItemStack template;
        public final List<Need> consumers = new ArrayList<>();
        public final List<Source> sources = new ArrayList<>();
        public int available = 0;

        public Group(ItemStack template) {
            this.template = template;
        }
    }
}