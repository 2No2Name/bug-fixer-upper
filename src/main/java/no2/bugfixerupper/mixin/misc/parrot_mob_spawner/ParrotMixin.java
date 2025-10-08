package no2.bugfixerupper.mixin.misc.parrot_mob_spawner;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Parrot.class)
public class ParrotMixin {

    @ModifyReceiver(
            method = "checkParrotSpawnRules",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private static BlockState useCanonicalAir(BlockState original, TagKey<?> tagKey) {
        if (original.isAir()) {
            return Blocks.AIR.defaultBlockState();
        }
        return original;
    }
}
