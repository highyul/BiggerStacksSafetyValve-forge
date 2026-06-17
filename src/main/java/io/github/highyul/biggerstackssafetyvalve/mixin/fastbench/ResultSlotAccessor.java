package io.github.highyul.biggerstackssafetyvalve.mixin.fastbench;

import net.minecraft.world.inventory.ResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResultSlot.class)
public interface ResultSlotAccessor {


    @Accessor("removeCount")
    int getRemoveCount();


    @Accessor("removeCount")
    void setRemoveCount(int removeCount);
}