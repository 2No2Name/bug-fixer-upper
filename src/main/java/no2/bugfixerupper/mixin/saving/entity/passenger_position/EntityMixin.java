package no2.bugfixerupper.mixin.saving.entity.passenger_position;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract @Nullable Entity getVehicle();

    @ModifyExpressionValue(
            method = "saveWithoutId(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;vehicle:Lnet/minecraft/world/entity/Entity;", ordinal = 0)
    )
    private Entity usePassengerPositionForSaving(Entity original) {
        return null;
    }
}
