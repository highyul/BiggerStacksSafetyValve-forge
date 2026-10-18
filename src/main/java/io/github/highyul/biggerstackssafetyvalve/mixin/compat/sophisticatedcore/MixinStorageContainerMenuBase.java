package io.github.highyul.biggerstackssafetyvalve.mixin.compat.sophisticatedcore;



import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = StorageContainerMenuBase.class, remap = false)
public class MixinStorageContainerMenuBase {

    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

    @Redirect(
            method = "doClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/common/gui/StorageContainerMenuBase;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack redirectQuickMoveStack(StorageContainerMenuBase menu, Player player, int slotIndex) {
        if (!this.biggerstackssafetyvalve$craftCounter.tryIncrementForTick(player.level().getGameTime())) {
            return ItemStack.EMPTY;
        }
        return menu.quickMoveStack(player, slotIndex);
    }
}