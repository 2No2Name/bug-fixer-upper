package no2.bugfixerupper.mixin.client.misc.leashed_player_vehicle;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Unique
    private <E extends Entity & Leashable> void clientLeashTick() {
        //Vanilla code adapted for client side
        //noinspection unchecked,DataFlowIssue
        E entity = (E) (Object) this;
        Leashable.LeashData leashData = entity.getLeashData();
        if (leashData != null && leashData.leashHolder != null) {
            Entity holder = entity.getLeashHolder();
            if (holder != null && holder.level() == entity.level()) {
                double d = entity.leashDistanceTo(holder);
                entity.whenLeashedTo(holder);
                if (d > entity.leashSnapDistance()) {
                    //breaking leash, handled server side
                } else if (d > entity.leashElasticDistance() - (double) holder.getBbWidth() - (double) entity.getBbWidth()
                        && /*CLIENT SIDE RELEVANT PUSHING:*/ entity.checkElasticInteractions(holder, leashData)) {
                    //fall damage + ai stuff, handled server side
                } else {
                    //pathfinding leash, handled server side
                }

                entity.setYRot((float) ((double) entity.getYRot() - leashData.angularMomentum));
                leashData.angularMomentum = leashData.angularMomentum * (double) Leashable.angularFriction(entity);
            }
        }
    }
}
