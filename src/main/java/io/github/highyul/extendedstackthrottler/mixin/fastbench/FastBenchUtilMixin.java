package io.github.highyul.extendedstackthrottler.mixin.fastbench;


import dev.shadowsoffire.fastbench.util.CraftingInventoryExt;
import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import static io.github.highyul.extendedstackthrottler.Config.MAX_CRAFT_COUNT;

@Mixin(value = dev.shadowsoffire.fastbench.util.FastBenchUtil.class, remap = false)
public class FastBenchUtilMixin {




    @Unique
    private static int extendedstackthrottlermax$Craft() {
        return MAX_CRAFT_COUNT.get();
    }



    /**
     * @author highyul
     * @reason When the limit is reached, completely halt synchronization with the result container and stop the FastBench (vanilla) call loop
     */
    @SuppressWarnings({"resource"})
    @Overwrite
    public static ItemStack handleShiftCraft(Player player, AbstractContainerMenu container, Slot resultSlot, CraftingInventoryExt craftMatrix, ResultContainer craftResult, FastBenchUtil.OutputMover mover) {
        ItemStack outputCopy = ItemStack.EMPTY;
        if (resultSlot != null && resultSlot.hasItem()) {
            craftMatrix.checkChanges = false;


            //Originally: Recipe<CraftingContainer> recipe = craftResult.getRecipeUsed();
            @SuppressWarnings("unchecked")
            Recipe<net.minecraft.world.inventory.CraftingContainer> recipe =
                    (Recipe<net.minecraft.world.inventory.CraftingContainer>) craftResult.getRecipeUsed();


            //Add
            int craftLoopCount = 0;


            while(recipe != null && recipe.matches(craftMatrix, player.level())) {

                //Add
                if (craftLoopCount >= extendedstackthrottlermax$Craft()) {
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


                //Originally: ((ResultSlot)resultSlot).removeCount += outputCopy.getCount();
                ResultSlotAccessor accessor = (ResultSlotAccessor) resultSlot;
                int currentRemoveCount = accessor.getRemoveCount();
                accessor.setRemoveCount(currentRemoveCount + outputCopy.getCount());


                resultSlot.onTake(player, recipeOutput);
            }

            craftMatrix.checkChanges = true;


            //Originally: slotChangedCraftingGrid(player.level(), player, craftMatrix, craftResult);
            FastBenchUtil.slotChangedCraftingGrid(player.level(), player, craftMatrix, craftResult);


        }

        return outputCopy;
    }
}

