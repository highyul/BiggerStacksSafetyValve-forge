package io.github.highyul.biggerstackssafetyvalve.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BiggerStacksSafetyValveMixinConfig implements IMixinConfigPlugin {

    private boolean hasAE2;

    private boolean hasFastBench;

    private boolean hasJei;

    private static final String VANILLA_MIXIN = "MixinAbstractContainerMenu";

    private static final List<String> AE2_MIXIN = List.of("MixinAEBaseMenu", "MixinAppEngSlot", "MixinCraftingTermSlot");

    private static final String FAST_BENCH_MIXIN = "FastBenchUtilMixin";

    private static final String JEI_MIXIN = "MixinBasicRecipeTransferHandlerServer";



    @Override
    public void onLoad(String mixinPackage) {

        this.hasAE2 =
                LoadingModList.get().getModFileById("ae2") != null;

        this.hasFastBench =
                LoadingModList.get().getModFileById("fastbench") != null;

        this.hasJei =
                LoadingModList.get().getModFileById("jei") != null;

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if (mixinClassName.endsWith(VANILLA_MIXIN)) {
            return !hasFastBench;
        }

        if (mixinClassName.endsWith(FAST_BENCH_MIXIN)) {
            return hasFastBench;
        }

        if (AE2_MIXIN.stream().anyMatch(mixinClassName::endsWith)) {
            return this.hasAE2;
        }

        if (mixinClassName.endsWith(JEI_MIXIN)) {
            return this.hasJei;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass,
                         String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass,
                          String mixinClassName, IMixinInfo mixinInfo) {
    }
}