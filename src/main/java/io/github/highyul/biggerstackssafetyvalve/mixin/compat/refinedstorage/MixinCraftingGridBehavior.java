package io.github.highyul.biggerstackssafetyvalve.mixin.compat.refinedstorage;


import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.grid.CraftingGridBehavior;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("resource")
@Mixin(value = CraftingGridBehavior.class, remap = false)
public class MixinCraftingGridBehavior {


    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();



    @Redirect(
            method = "onCraftedShift",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/refinedmods/refinedstorage/api/util/IComparer;isEqual(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean redirectOnCraftedShift(IComparer comparer, ItemStack stack1, ItemStack stack2,
                                          INetworkAwareGrid grid, Player player) {
        if (!biggerstackssafetyvalve$craftCounter.tryIncrementForTick(player.level().getGameTime())) {
            return false;
        }
        return comparer.isEqual(stack1, stack2);
    }
}