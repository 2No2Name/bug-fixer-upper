package no2.bugfixerupper.mixin.misc.shield_projectiles;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    )
    private Vec3 fixFlightFromPosVector(Vec3 projectilePos, Vec3 entityPos, Operation<Vec3> original, @Local(argsOnly = true) DamageSource damageSource) {
        Vec3 fromVectorAdjustment = Vec3.ZERO;
        if (damageSource.getDirectEntity() instanceof Projectile projectile) {
            Vec3 deltaMovement = projectile.getDeltaMovement();
            double horizontalSize = Math.max(this.getBoundingBox().getXsize(), this.getBoundingBox().getZsize());
            fromVectorAdjustment = deltaMovement.normalize().scale(-2f * horizontalSize);
        }
        return original.call(projectilePos, entityPos).add(fromVectorAdjustment);
    }
}
