package no2.bugfixerupper.mixin.misc.exit_end_portal_torches;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndPodiumFeature.class)
public class EndPodiumFeatureMixin {

    @WrapWithCondition(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/EndPodiumFeature;setBlock(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
                    ordinal = 2
            )
    )
    private boolean placeIfNoTorchesHanging(EndPodiumFeature instance, LevelWriter levelWriter, BlockPos blockPos, BlockState blockState, @Local(argsOnly = true) FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        return !blockPos.equals(featurePlaceContext.origin().above(2));
    }
}
