package no2.bugfixerupper.mixin.client.misc.clickable_projectile_size;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @WrapOperation(
            method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getPickRadius()F")
    )
    private static float fixPickRadiusForCreativePlayer(Entity clickableProjectile, Operation<Float> original, @Local(argsOnly = true) Entity clickingEntity) {
        float result = original.call(clickableProjectile);
        if (result == 1.0F && clickableProjectile.level().isClientSide() && clickableProjectile instanceof Projectile && clickableProjectile.isPickable() && clickingEntity instanceof Player player) {
            if (player.isCreative()) {
                return 0.0F;
            }
        }
        return result;
    }
}
