package io.github.highyul.biggerstackssafetyvalve.mixin.ae2;

import appeng.menu.AEBaseMenu;
import appeng.helpers.InventoryAction;
import io.github.highyul.biggerstackssafetyvalve.ducks.IHasCraftLoopCounter;
import io.github.highyul.biggerstackssafetyvalve.util.CraftLoopCounter;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AEBaseMenu.class)
public class MixinAEBaseMenu implements IHasCraftLoopCounter {


    @Unique
    private final CraftLoopCounter biggerstackssafetyvalve$craftCounter = new CraftLoopCounter();

    @Override
    public CraftLoopCounter biggerstackssafetyvalve$getCraftCounter() {
        return this.biggerstackssafetyvalve$craftCounter;
    }


    @Inject(method = "doAction", at = @At("HEAD"), remap = false)
    private void onDoActionHead(ServerPlayer player, InventoryAction action, int slot, long id, CallbackInfo ci) {
        this.biggerstackssafetyvalve$craftCounter.reset();
    }
}