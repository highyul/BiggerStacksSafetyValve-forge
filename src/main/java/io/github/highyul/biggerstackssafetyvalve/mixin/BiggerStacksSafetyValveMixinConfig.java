package io.github.highyul.biggerstackssafetyvalve.mixin;

import java.util.List;
import java.util.Set;


import io.github.highyul.biggerstackssafetyvalve.config.StaticConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class BiggerStacksSafetyValveMixinConfig implements IMixinConfigPlugin {


    @Override
    public void onLoad(String mixinPackage) {
        StaticConfig.loadEarly();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        for (ModConfigTarget target : ModConfigTarget.values()) {
            if (target.matches(mixinClassName)) {
                return target.shouldApply();
            }
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}