package io.github.highyul.biggerstackssafetyvalve;



import com.mojang.logging.LogUtils;
import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfig;
import io.github.highyul.biggerstackssafetyvalve.config.RuntimeConfigWatcher;
import io.github.highyul.biggerstackssafetyvalve.config.StaticConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;


@Mod(BiggerStacksSafetyValve.MODID)
public class BiggerStacksSafetyValve {

    public static final String MODID = "biggerstackssafetyvalve";

    public static final Logger LOGGER = LogUtils.getLogger();

    public BiggerStacksSafetyValve(FMLJavaModLoadingContext context) {

        context.registerConfig(
                ModConfig.Type.COMMON,
                StaticConfig.SPEC,
                "biggerstackssafetyvalve/mixin_compat.toml"
        );

        Path runtimePath =
                FMLPaths.CONFIGDIR.get()
                        .resolve("biggerstackssafetyvalve")
                        .resolve("shiftClickCraftLimit.toml");

        RuntimeConfig.load(runtimePath);

        new RuntimeConfigWatcher(runtimePath)
                .start();

    }

}
