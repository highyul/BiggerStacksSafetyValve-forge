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
