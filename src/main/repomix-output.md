This file is a merged representation of the entire codebase, combined into a single document by Repomix.

# File Summary

## Purpose
This file contains a packed representation of the entire repository's contents.
It is designed to be easily consumable by AI systems for analysis, code review,
or other automated processes.

## File Format
The content is organized as follows:
1. This summary section
2. Repository information
3. Directory structure
4. Repository files (if enabled)
5. Multiple file entries, each consisting of:
  a. A header with the file path (## File: path/to/file)
  b. The full contents of the file in a code block

## Usage Guidelines
- This file should be treated as read-only. Any changes should be made to the
  original repository files, not this packed version.
- When processing this file, use the file path to distinguish
  between different files in the repository.
- Be aware that this file may contain sensitive information. Handle it with
  the same level of security as you would the original repository.

## Notes
- Some files may have been excluded based on .gitignore rules and Repomix's configuration
- Binary files are not included in this packed representation. Please refer to the Repository Structure section for a complete list of file paths, including binary files
- Files matching patterns in .gitignore are excluded
- Files matching default ignore patterns are excluded
- Files are sorted by Git change count (files with more changes are at the bottom)

# Directory Structure
```
java/io/github/highyul/biggerstackssafetyvalve/BiggerStacksSafetyValve.java
java/io/github/highyul/biggerstackssafetyvalve/Config.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/BiggerStacksSafetyValveMixinConfig.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/ae2/MixinCraftingTermSlot.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/fastbench/OverwriteMixinFastBenchUtil.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/fastbench/ResultSlotAccessor.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/jei/OverwriteMixinBasicRecipeTransferHandlerServer.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/rs/MixinCraftingGridBehavior.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/tomsstorage/MixinCraftingTerminalMenu.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/ModConfigTarget.java
java/io/github/highyul/biggerstackssafetyvalve/mixin/vanilla/MixinAbstractContainerMenu.java
java/io/github/highyul/biggerstackssafetyvalve/util/CraftLimitHelper.java
java/io/github/highyul/biggerstackssafetyvalve/util/CraftLoopCounter.java
java/io/github/highyul/biggerstackssafetyvalve/util/jei/TransferHelper.java
resources/biggerstackssafetyvalve.mixins.json
resources/biggerstackssafetyvalve.png
resources/META-INF/mods.toml
resources/pack.mcmeta
```

# Files

## File: java/io/github/highyul/biggerstackssafetyvalve/BiggerStacksSafetyValve.java
```java
package io.github.highyul.biggerstackssafetyvalve;



import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(BiggerStacksSafetyValve.MODID)
public class BiggerStacksSafetyValve {

    public static final String MODID = "biggerstackssafetyvalve";

    public BiggerStacksSafetyValve(FMLJavaModLoadingContext context) {

        context.registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );
    }

}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/Config.java
```java
package io.github.highyul.biggerstackssafetyvalve;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MAX_CRAFT_COUNT;


    private static final Map<String, ForgeConfigSpec.BooleanValue> MOD_MIXIN_SWITCHES = new HashMap<>();


    private static final Map<String, Boolean> EARLY_CACHE = new HashMap<>();
    private static boolean isEarlyLoaded = false;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");
        MAX_CRAFT_COUNT = builder
                .comment("Maximum number of crafts allowed per shift-click operation.")
                .defineInRange("maxCraftCount", 8192, 4, Integer.MAX_VALUE);
        builder.pop();


        builder.push("mixin_compatibility");
        builder.comment("Force disable mixins for specific mods if they cause compatibility issues.");

        String[] mods = {"fastbench", "ae2", "jei", "refinedstorage","tomsstorage"};
        for (String modId : mods) {
            ForgeConfigSpec.BooleanValue configValue = builder
                    .comment("Enable mixins for " + modId)
                    .define("enable_" + modId, true);
            MOD_MIXIN_SWITCHES.put(modId, configValue);
        }
        builder.pop();

        SPEC = builder.build();
    }


    public static boolean isModMixinEnabled(String modId) {
        if (!isEarlyLoaded) {
            ForgeConfigSpec.BooleanValue value = MOD_MIXIN_SWITCHES.get(modId);
            return value == null || value.get();
        }
        return EARLY_CACHE.getOrDefault(modId, true);
    }


    public static void loadEarly() {
        if (isEarlyLoaded) return;


        Path configPath = FMLPaths.CONFIGDIR.get().resolve("biggerstackssafetyvalve-common.toml");

        try {
            CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            configData.load();


            for (String modId : MOD_MIXIN_SWITCHES.keySet()) {
                boolean enabled = configData.getOrElse("mixin_compatibility.enable_" + modId, true);
                EARLY_CACHE.put(modId, enabled);
            }

            configData.close();
        } catch (Exception e) {
            for (String modId : MOD_MIXIN_SWITCHES.keySet()) {
                EARLY_CACHE.put(modId, true);
            }
        }

        isEarlyLoaded = true;
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/BiggerStacksSafetyValveMixinConfig.java
```java
package io.github.highyul.biggerstackssafetyvalve.mixin;

import java.util.List;
import java.util.Set;


import io.github.highyul.biggerstackssafetyvalve.Config;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class BiggerStacksSafetyValveMixinConfig implements IMixinConfigPlugin {


    @Override
    public void onLoad(String mixinPackage) {
        Config.loadEarly();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        for (ModConfigTarget target : ModConfigTarget.values()) {
            if (target.matches(mixinClassName)) {
                return target.shouldApply();
            }
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/ae2/MixinCraftingTermSlot.java
```java
package io.github.highyul.biggerstackssafetyvalve.mixin.compat.ae2;

import appeng.menu.slot.CraftingTermSlot;
import appeng.helpers.InventoryAction;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@SuppressWarnings("resource")
@Mixin(value = CraftingTermSlot.class, remap = false)
public class MixinCraftingTermSlot {

    @Unique
    private long biggerstackssafetyvalve$lastTick = -1L;

    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$counter = new CraftLoopCounter();

    @Inject(
            method = "doClick",
            at = @At(value = "INVOKE", target = "Lappeng/menu/slot/CraftingTermSlot;craftItem(Lnet/minecraft/world/entity/player/Player;Lappeng/api/storage/MEStorage;Lappeng/api/stacks/KeyCounter;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true
    )
    private void onCraftItem(
            InventoryAction action,
            Player who,
            CallbackInfo ci
    ) {

        if (who.level().isClientSide) return;

        long tick = who.level().getGameTime();

        if (tick != biggerstackssafetyvalve$lastTick) {
            biggerstackssafetyvalve$counter.reset();
            biggerstackssafetyvalve$lastTick = tick;
        }

        if (!biggerstackssafetyvalve$counter.tryIncrement()) {
            ci.cancel();
        }
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/fastbench/OverwriteMixinFastBenchUtil.java
```java
package io.github.highyul.biggerstackssafetyvalve.mixin.compat.fastbench;


import dev.shadowsoffire.fastbench.util.CraftingInventoryExt;
import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLimitHelper;
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


            //Originally: Recipe<CraftingContainer> recipe = craftResult.getRecipeUsed();
            @SuppressWarnings("unchecked")
            Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>) craftResult.getRecipeUsed();


            //Add
            int craftLoopCount = 0;


            while(recipe != null && recipe.matches(craftMatrix, player.level())) {

                //Add
                if (craftLoopCount >= CraftLimitHelper.getLimit()) {
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
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/fastbench/ResultSlotAccessor.java
```java
package io.github.highyul.biggerstackssafetyvalve.mixin.compat.fastbench;

import net.minecraft.world.inventory.ResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ResultSlot.class, remap = false)
public interface ResultSlotAccessor {


    @Accessor("removeCount")
    int getRemoveCount();


    @Accessor("removeCount")
    void setRemoveCount(int removeCount);
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/jei/OverwriteMixinBasicRecipeTransferHandlerServer.java

```java
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

  @Unique
  private final TransferHelper biggerstackssafetyvalve$TransferHelper = new TransferHelper();


  /**
   * @author YourName
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
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/rs/MixinCraftingGridBehavior.java

```java
package io.github.highyul.biggerstackssafetyvalve.mixin.compat.refinedstorage;


import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid;
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


    @Unique
    private long biggerstackssafetyvalve$lastCraftTick = -1L;


    @Redirect(
            method = "onCraftedShift",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/refinedmods/refinedstorage/api/util/IComparer;isEqual(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean redirectLoopCondition(
            com.refinedmods.refinedstorage.api.util.IComparer comparer,
            ItemStack stack1,
            ItemStack stack2,

            INetworkAwareGrid grid,
            Player player
    ) {


        long currentTick = player.level().getGameTime();

        if (currentTick != biggerstackssafetyvalve$lastCraftTick) {
            biggerstackssafetyvalve$craftCounter.reset();
            biggerstackssafetyvalve$lastCraftTick = currentTick;
        }

        if (!biggerstackssafetyvalve$craftCounter.tryIncrement()) {
            return false;
        }

        return comparer.isEqual(stack1, stack2);
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/compat/tomsstorage/MixinCraftingTerminalMenu.java

```java
package io.github.highyul.biggerstackssafetyvalve.mixin.compat.toms_storage;

import com.tom.storagemod.gui.CraftingTerminalMenu;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("resource")
@Mixin(value = CraftingTerminalMenu.class, remap = false)
public class MixinCraftingTerminalMenu {

  @Unique
  private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

  @Unique
  private long biggerstackssafetyvalve$lastCraftTick = -1L;


  @Inject(
          method = "shiftClickItems(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;",
          at = @At("HEAD"),
          cancellable = true
  )
  private void onShiftClickItemsHead(
          Player player,
          int index,
          CallbackInfoReturnable<ItemStack> cir
  ) {
    if (player.level().isClientSide) {
      return;
    }

    long currentTick = player.level().getGameTime();

    if (currentTick != biggerstackssafetyvalve$lastCraftTick) {
      biggerstackssafetyvalve$craftCounter.reset();
      biggerstackssafetyvalve$lastCraftTick = currentTick;
    }

    if (!biggerstackssafetyvalve$craftCounter.tryIncrement()) {
      cir.setReturnValue(ItemStack.EMPTY);
    }
  }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/ModConfigTarget.java
```java
package io.github.highyul.biggerstackssafetyvalve.mixin;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraftforge.fml.loading.LoadingModList;

public enum ModConfigTarget {
    VANILLA(List.of("vanilla"), () -> {
        boolean fastBenchActive = isLoaded("fastbench") && isConfigEnabled("fastbench");
        return !fastBenchActive;
    }),
    FAST_BENCH(List.of("fastbench"), () -> isLoaded("fastbench") && isConfigEnabled("fastbench")),
    AE2(List.of("ae2"), () -> isLoaded("ae2") && isConfigEnabled("ae2")),
    JEI(List.of("jei"), () -> isLoaded("jei") && isConfigEnabled("jei")),
    RS(List.of("rs"), () -> isLoaded("refinedstorage") && isConfigEnabled("refinedstorage")),
    TOMS_STORAGE(List.of("toms_storage", "tomsstorage"), () -> isLoaded("toms_storage") && isConfigEnabled("toms_storage"));


    private final List<String> mixins;
    private final BooleanSupplier condition;

    ModConfigTarget(List<String> mixins, BooleanSupplier condition) {
        this.mixins = mixins;
        this.condition = condition;
    }

    public boolean matches(String mixinClassName) {
        return mixins.stream().anyMatch(mixinClassName::contains);
    }

    public boolean shouldApply() {
        return condition.getAsBoolean();
    }

    private static boolean isLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

    private static boolean isConfigEnabled(String modId) {
        return io.github.highyul.biggerstackssafetyvalve.Config.isModMixinEnabled(modId);
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/mixin/vanilla/MixinAbstractContainerMenu.java
```java
package io.github.highyul.biggerstackssafetyvalve.mixin.vanilla;


import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("resource")
@Mixin(value = AbstractContainerMenu.class, remap = false)
public class MixinAbstractContainerMenu {


    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();


    @Unique
    private long biggerstackssafetyvalve$lastCraftTick = -1L;


    @Redirect(
            method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack redirectQuickMoveStack(AbstractContainerMenu menu, Player player, int slotIndex) {

        long currentTick = player.level().getGameTime();

        if (currentTick != biggerstackssafetyvalve$lastCraftTick) {
            biggerstackssafetyvalve$craftCounter.reset();
            biggerstackssafetyvalve$lastCraftTick = currentTick;
        }


        if (!biggerstackssafetyvalve$craftCounter.tryIncrement()) {
            return ItemStack.EMPTY;
        }
        return menu.quickMoveStack(player, slotIndex);
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/util/CraftLimitHelper.java
```java
package io.github.highyul.biggerstackssafetyvalve.util;

import io.github.highyul.biggerstackssafetyvalve.Config;

public final class CraftLimitHelper {

    private CraftLimitHelper() {}

    public static int getLimit() {
        return Config.MAX_CRAFT_COUNT.get();
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/util/CraftLoopCounter.java
```java
package io.github.highyul.biggerstackssafetyvalve.util;

public class CraftLoopCounter {

    private int count;

    public void reset() {
        count = 0;
    }

    public int getCount() {
        return count;
    }


    public boolean tryIncrement() {
        if (count >= CraftLimitHelper.getLimit()) {
            return false;
        }

        count++;
        return true;
    }
}
```

## File: java/io/github/highyul/biggerstackssafetyvalve/util/jei/TransferHelper.java
```java
package io.github.highyul.biggerstackssafetyvalve.util.jei;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class TransferHelper {

    public static final class Need {
        public final Slot craftingSlot;
        public final ItemStack item;

        public Need(Slot craftingSlot, ItemStack item) {
            this.craftingSlot = craftingSlot;
            this.item = item;
        }
    }

    public static final class Source {
        public final Slot slot;
        public final int amount;

        public Source(Slot slot, int amount) {
            this.slot = slot;
            this.amount = amount;
        }
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
```

## File: resources/biggerstackssafetyvalve.mixins.json
```json
{
  "required": false,
  "minVersion": "0.8",
  "package": "io.github.highyul.biggerstackssafetyvalve.mixin",
  "compatibilityLevel": "JAVA_17",
  "refmap": "biggerstackssafetyvalve.refmap.json",

  "plugin": "io.github.highyul.biggerstackssafetyvalve.mixin.BiggerStacksSafetyValveMixinConfig",

  "mixins": [
    "vanilla.MixinAbstractContainerMenu",
    "compat.fastbench.OverwriteMixinFastBenchUtil",
    "compat.fastbench.ResultSlotAccessor",
    "compat.ae2.MixinCraftingTermSlot",
    "compat.jei.OverwriteMixinBasicRecipeTransferHandlerServer",
    "compat.rs.MixinCraftingGridBehavior",
    "compat.tomsstorage.MixinCraftingTerminalMenu"
  ],

  "client": [],

  "injectors": {
    "defaultRequire": 1
  },

  "overwrites": {
    "requireAnnotations": true
  }
}
```

## File: resources/META-INF/mods.toml
```toml
modLoader = "javafml"
loaderVersion = "${loader_version_range}"
license = "${mod_license}"
[[mods]]
modId = "${mod_id}"
version = "${mod_version}"
displayName = "${mod_name}"
logoFile="biggerstackssafetyvalve.png"
authors = "${mod_authors}"
description = '''
${mod_description}
'''
[[dependencies."${mod_id}"]]
modId = "forge"
mandatory = true
versionRange = "${forge_version_range}"
ordering = "NONE"
side = "BOTH"
[[dependencies."${mod_id}"]]
modId = "minecraft"
mandatory = true
versionRange = "${minecraft_version_range}"
ordering = "NONE"
side = "BOTH"
```

## File: resources/pack.mcmeta
```
{
  "pack": {
    "description": "biggerstackssafetyvalve resources",
    "pack_format": 15
  }
}
```
