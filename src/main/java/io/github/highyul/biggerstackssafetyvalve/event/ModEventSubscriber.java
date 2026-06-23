package io.github.highyul.biggerstackssafetyvalve.event;



import io.github.highyul.biggerstackssafetyvalve.BiggerStacksSafetyValve;
import io.github.highyul.biggerstackssafetyvalve.command.CraftingLimitsCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = BiggerStacksSafetyValve.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ModEventSubscriber {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CraftingLimitsCommand.register(event.getDispatcher());
    }
}
