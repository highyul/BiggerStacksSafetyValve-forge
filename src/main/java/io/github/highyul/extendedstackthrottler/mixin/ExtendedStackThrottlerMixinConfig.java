package io.github.highyul.extendedstackthrottler.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ExtendedStackThrottlerMixinConfig implements IMixinConfigPlugin {

    private boolean hasFastBench;

    @Override
    public void onLoad(String mixinPackage) {
        this.hasFastBench =
                LoadingModList.get().getModFileById("fastbench") != null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if (mixinClassName.endsWith("MixinAbstractContainerMenu")) {
            return !hasFastBench;
        }

        if (mixinClassName.endsWith("FastBenchUtilMixin")) {
            return hasFastBench;
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