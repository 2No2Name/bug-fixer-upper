package no2.bugfixerupper.mixin.chunkload.item_frame_sound;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {

    @WrapWithCondition(
            method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/ItemFrame;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
            )
    )
    private boolean shouldPlaySound(ItemFrame instance, SoundEvent soundEvent, float v, float w, @Local(argsOnly = true) boolean update) {
        return update; //Only generate a sound when the item is not placed by chunk loading
    }
}
