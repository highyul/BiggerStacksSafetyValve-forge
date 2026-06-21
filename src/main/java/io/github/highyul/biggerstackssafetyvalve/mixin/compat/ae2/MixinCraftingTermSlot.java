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
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

    @Inject(
            method = "doClick",
            at = @At(value = "INVOKE", target = "Lappeng/menu/slot/CraftingTermSlot;craftItem(Lnet/minecraft/world/entity/player/Player;Lappeng/api/storage/MEStorage;Lappeng/api/stacks/KeyCounter;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true
    )
    private void onCraftItem(InventoryAction action, Player who, CallbackInfo ci) {
        if (who.level().isClientSide) return;
        if (!biggerstackssafetyvalve$craftCounter.tryIncrementForTick(who.level().getGameTime())) {
            ci.cancel();
        }
    }
}