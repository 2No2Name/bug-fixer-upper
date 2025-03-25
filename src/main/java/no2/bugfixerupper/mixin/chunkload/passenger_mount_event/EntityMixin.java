package no2.bugfixerupper.mixin.chunkload.passenger_mount_event;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.GameEvent;
import no2.bugfixerupper.common.threadlocals.MountEventSilencer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @WrapWithCondition(
            method = "addPassenger(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;)V")
    )
    private boolean shouldEmitMountEvent(Entity thisInstance, Holder<GameEvent> holder, @Nullable Entity passenger) {
        return !MountEventSilencer.isMountEventSilenced();
    }
}
