package no2.bugfixerupper.mixin.client.misc.leashed_player_vehicle;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract Level level();

    @Shadow public abstract float distanceTo(Entity entity);

    @Shadow public abstract boolean onGround();

    @Shadow public abstract boolean isLocalInstanceAuthoritative();

    @Inject(
            method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V")
    )
    private <E extends Entity & Leashable> void clientSideLeashTickIfNeeded(CallbackInfo ci) {
        if (this.level().isClientSide() && this.isLocalInstanceAuthoritative() && this instanceof Leashable) {
            this.clientLeashTick();
        }
    }

    @Unique
    private void clientLeashTick() {
        Leashable entity = (Leashable) this;
        Entity holder = entity.getLeashHolder();
        if (holder != null) {
            float distance = this.distanceTo(holder);

            if (distance > 6f) {
                //noinspection ConstantValue
                if (this.onGround() && !(entity instanceof AbstractBoat)) { //AbstractBoat has its own smooth implementation of elasticRangeLeashBehavior
                    if (distance < 6.25f) {
                        //Smooth out the cutoff when on the ground to avoid glitchy / bouncy leads, e.g. when riding a leashed horse. When not on the ground, leads are supposed to be bouncy.
                        double lerp = ((distance - 6.0f) * 4); //0 at 6.0, 1 at 6.25, i.e. 4 pixels in
                        //Pull strength is divided by distance squared, by multiplying 1/sqrt(lerp), the result is multiplied by lerp
                        //Using a linear lerp because this is minecart, which lerps so many things.
                        distance *= (float) (1.0 / (Math.sqrt(lerp)));
                    }
                }
                entity.elasticRangeLeashBehaviour(holder, distance);
            }
        }
    }
}
