package io.github.highyul.biggerstackssafetyvalve.mixin.vanilla;


import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu {


    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$counter = new CraftLoopCounter();



    @Inject(
            method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD")
    )
    private void onDoClickHead(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        biggerstackssafetyvalve$counter.reset();

    }


    @Redirect(
            method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack redirectQuickMoveStack(AbstractContainerMenu menu, Player player, int slotIndex) {
        if (!biggerstackssafetyvalve$counter.tryIncrement()) {
            return ItemStack.EMPTY;
        }
        return menu.quickMoveStack(player, slotIndex);
    }
}
