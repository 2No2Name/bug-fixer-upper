package no2.bugfixerupper.mixin.tickfreeze.passengerfreeze;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow public abstract TickRateManager tickRateManager();

    @Inject(
            method = "tickPassenger", at = @At("HEAD"), cancellable = true
    )
    private void checkTickFreeze(Entity vehicle, Entity passenger, CallbackInfo ci) {
        if (this.tickRateManager().isEntityFrozen(passenger)) {
            ci.cancel();
        }
    }
}
