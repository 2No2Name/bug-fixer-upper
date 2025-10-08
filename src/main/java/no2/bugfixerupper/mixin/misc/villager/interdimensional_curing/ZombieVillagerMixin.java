package no2.bugfixerupper.mixin.misc.villager.interdimensional_curing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(ZombieVillager.class)
public class ZombieVillagerMixin {

    @WrapOperation(
            method = "method_63659",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/world/entity/player/Player;")
    )
    private Player getPlayerInAllWorlds(ServerLevel instance, UUID uuid, Operation<Player> original, @Local(argsOnly = true) Villager villager) {
        Player result = original.call(instance, uuid);
        if (result == null) {
            result = instance.getServer().getPlayerList().getPlayer(uuid);
        }
        if (result == null) {
            //Player is offline, do the gossip anyway -> gossip (cheaper trade prices) is not lost, but advancement is still lost
            villager.getGossips().add(uuid, GossipType.MAJOR_POSITIVE, 20);
            villager.getGossips().add(uuid, GossipType.MINOR_POSITIVE, 25);
        }
        return result;
    }
}
