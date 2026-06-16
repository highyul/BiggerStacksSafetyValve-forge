package io.github.highyul.extendedstackthrottler;



import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(ExtendedStackThrottler.MODID)
public class ExtendedStackThrottler {

    public static final String MODID = "extendedstackthrottler";

    public ExtendedStackThrottler(FMLJavaModLoadingContext context) {

        context.registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );
    }

}
