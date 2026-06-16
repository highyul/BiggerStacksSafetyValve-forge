package io.github.highyul.extendedstackthrottler.mixin.vanilla;


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

import static io.github.highyul.extendedstackthrottler.Config.MAX_CRAFT_COUNT;


@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu {


    @Unique
    private static int extendedstackthrottlermax$Craft() {
        return MAX_CRAFT_COUNT.get();
    }

    @Unique
    private int extendedstackthrottler$quickMoveLoopCount = 0;


    @Inject(
            method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD")
    )
    private void onDoClickHead(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        this.extendedstackthrottler$quickMoveLoopCount = 0;

    }


    @Redirect(
            method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack redirectQuickMoveStack(AbstractContainerMenu menu, Player player, int slotIndex) {
        if (this.extendedstackthrottler$quickMoveLoopCount >= extendedstackthrottlermax$Craft()) {
            return ItemStack.EMPTY;
        }
        this.extendedstackthrottler$quickMoveLoopCount++;
        return menu.quickMoveStack(player, slotIndex);
    }
}
