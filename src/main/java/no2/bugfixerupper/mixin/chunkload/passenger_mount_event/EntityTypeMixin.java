package no2.bugfixerupper.mixin.chunkload.passenger_mount_event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import no2.bugfixerupper.common.threadlocals.MountEventSilencer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityType.class)
public class EntityTypeMixin {


    @Redirect(
            method = "method_17843",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;startRiding(Lnet/minecraft/world/entity/Entity;Z)Z")
    )
    private static boolean startRidingSilently(Entity passenger, Entity vehicle, boolean bl) {
        MountEventSilencer.silenceMountEvent();
        boolean ret = passenger.startRiding(vehicle, bl);
        MountEventSilencer.unsilenceMountEvent();
        return ret;
    }
}
