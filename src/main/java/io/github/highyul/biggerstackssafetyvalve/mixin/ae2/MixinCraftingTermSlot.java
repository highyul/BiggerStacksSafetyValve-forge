package io.github.highyul.biggerstackssafetyvalve.mixin.ae2;

import appeng.menu.slot.CraftingTermSlot;
import appeng.helpers.InventoryAction;
import appeng.menu.AEBaseMenu;
import io.github.highyul.biggerstackssafetyvalve.ducks.ae2.IAE2SlotAccessor;
import io.github.highyul.biggerstackssafetyvalve.ducks.IHasCraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;




@Mixin(CraftingTermSlot.class)
public class MixinCraftingTermSlot {



    @Inject(
            method = "doClick",
            at = @At(value = "INVOKE", target = "Lappeng/menu/slot/CraftingTermSlot;craftItem(Lnet/minecraft/world/entity/player/Player;Lappeng/api/storage/MEStorage;Lappeng/api/stacks/KeyCounter;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true,
            remap = false
    )
    private void onCraftItemInvoke(InventoryAction action, Player who, CallbackInfo ci) {


        if (((Object) this) instanceof IAE2SlotAccessor slotAccessor) {
            AEBaseMenu currentMenu = slotAccessor.biggerstackssafetyvalve$getMenu();


            if (currentMenu instanceof IHasCraftLoopCounter menuAccessor) {
                if (!menuAccessor
                        .biggerstackssafetyvalve$getCraftCounter()
                        .tryIncrement()) {

                    ci.cancel();
                }
            }
        }
    }


}