package io.github.highyul.biggerstackssafetyvalve.mixin.compat.fastbench;


import dev.shadowsoffire.fastbench.util.CraftingInventoryExt;
import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


@SuppressWarnings("resource")
@Mixin(value = dev.shadowsoffire.fastbench.util.FastBenchUtil.class, remap = false)
public class OverwriteMixinFastBenchUtil {


    /**
     * @author highyul
     * @reason When the limit is reached, completely halt synchronization with the result container and stop the FastBench (vanilla) call loop
     */
    @Overwrite
    public static ItemStack handleShiftCraft(Player player,
                                             AbstractContainerMenu container,
                                             Slot resultSlot,
                                             CraftingInventoryExt craftMatrix,
                                             ResultContainer craftResult,
                                             FastBenchUtil.OutputMover mover) {
        ItemStack outputCopy = ItemStack.EMPTY;
        if (resultSlot != null && resultSlot.hasItem()) {
            craftMatrix.checkChanges = false;



            @SuppressWarnings("unchecked")
            Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>) craftResult.getRecipeUsed();



            int craftLoopCount = 0;


            while(recipe != null && recipe.matches(craftMatrix, player.level())) {


                int craftingLimits = RuntimeConfig.getCraftingLimits();
                if (craftLoopCount >= craftingLimits) {
                    craftMatrix.checkChanges = true;
                    return ItemStack.EMPTY;
                }
                craftLoopCount++;


                ItemStack recipeOutput = recipe.assemble(craftMatrix, player.level().registryAccess());
                if (recipeOutput.isEmpty()) {
                    throw new RuntimeException("A recipe matched but produced an empty output - Offending Recipe : " + recipe.getId() + " - This is NOT a bug in FastWorkbench!");
                }

                outputCopy = recipeOutput.copy();
                recipeOutput.onCraftedBy(player.level(), player, 1);
                ForgeEventFactory.firePlayerCraftingEvent(player, recipeOutput, craftMatrix);


                if (!player.level().isClientSide && mover.merge(container, recipeOutput)) {
                    craftMatrix.checkChanges = true;
                    return ItemStack.EMPTY;
                }



                ResultSlotAccessor accessor = (ResultSlotAccessor) resultSlot;
                int currentRemoveCount = accessor.getRemoveCount();
                accessor.setRemoveCount(currentRemoveCount + outputCopy.getCount());


                resultSlot.onTake(player, recipeOutput);
            }

            craftMatrix.checkChanges = true;



            FastBenchUtil.slotChangedCraftingGrid(player.level(), player, craftMatrix, craftResult);


        }

        return outputCopy;
    }
}

