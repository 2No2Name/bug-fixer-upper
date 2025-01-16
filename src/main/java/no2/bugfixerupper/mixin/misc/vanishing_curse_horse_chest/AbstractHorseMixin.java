package no2.bugfixerupper.mixin.misc.vanishing_curse_horse_chest;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {

    @ModifyExpressionValue(
            method = "dropEquipment(Lnet/minecraft/server/level/ServerLevel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;has(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/component/DataComponentType;)Z"
            )
    )
    private boolean ignoreVanishingOnChestSlots(boolean original, @Local int i) {
        if (i != 0) {
            return false;
        }
        return original;
    }

}
