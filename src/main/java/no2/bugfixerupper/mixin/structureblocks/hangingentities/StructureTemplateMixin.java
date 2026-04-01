package no2.bugfixerupper.mixin.structureblocks.hangingentities;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.minecraft.world.level.block.Rotation.CLOCKWISE_90;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    //Fix combined use of rotation + mirror in structure blocks leading to wrong rotation of paintings and item frames
    @ModifyArg(method = "lambda$placeEntities$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rotate(Lnet/minecraft/world/level/block/Rotation;)F"))
    private static Rotation fixPaintingPlacement(Rotation rotation, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) Mirror mirror) {
        if (!(entity instanceof HangingEntity)) {
            return rotation;
        }

        if (mirror == Mirror.FRONT_BACK || mirror == Mirror.LEFT_RIGHT) {
            if (rotation == Rotation.CLOCKWISE_90) {
                return Rotation.COUNTERCLOCKWISE_90;
            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                return CLOCKWISE_90;
            }
        }

        return rotation;
    }
}
