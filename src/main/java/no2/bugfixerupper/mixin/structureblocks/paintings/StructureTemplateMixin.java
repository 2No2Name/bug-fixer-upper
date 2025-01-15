package no2.bugfixerupper.mixin.structureblocks.paintings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    // Original implementation by BluSpring https://github.com/2No2Name/bug-fixer-upper/issues/2
    @Inject(method = "method_17917", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;moveTo(DDDFF)V", shift = At.Shift.AFTER))
    private static void fixPaintingPlacement(Rotation rotation, Mirror mirror, Vec3 vec3, boolean bl, ServerLevelAccessor serverLevelAccessor, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof Painting painting)) {
            return;
        }

        adjustPaintingPosition(painting);
    }

    @Unique
    private static void adjustPaintingPosition(Painting painting) {
        boolean isEvenHeight = (Math.round(painting.getBoundingBox().getYsize()) % 2) == 0;
        boolean isEvenWidth = (Math.round(Math.max(painting.getBoundingBox().getXsize(), painting.getBoundingBox().getZsize())) % 2) == 0;

        BlockPos pos = painting.getPos();
        Direction direction = painting.getDirection();

        boolean changed = false;

        // paintings with an even height seem to always be moved upwards...
        if (isEvenHeight) {
            pos = pos.below();
            changed = true;
        }

        // paintings with an even width seem to be moved in the clockwise direction of their facing direction,
        // if they're west or south.
        if (isEvenWidth && (direction == Direction.WEST || direction == Direction.SOUTH)) {
            Vec3i moveTo = direction.getClockWise().getUnitVec3i();
            pos = pos.offset(moveTo);
            changed = true;
        }

        if (changed) {
            painting.setPos(pos.getCenter());
        }
    }
}
