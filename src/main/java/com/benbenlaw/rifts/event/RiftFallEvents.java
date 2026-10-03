package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.entity.NaturalRift;
import com.benbenlaw.rifts.entity.RiftsAttachments;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class RiftFallEvents {

    private static final int FALL_MARGIN = 16;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(RiftsWorldGen.RIFT_LEVEL) || player.getY() > level.getMinY() - FALL_MARGIN) return;

        ServerLevel target = level.getServer().overworld();
        net.minecraft.core.BlockPos spot = player.blockPosition();

        if (player.hasData(RiftsAttachments.RIFT_ENTRANCE)) {
            GlobalPos entrance = player.getData(RiftsAttachments.RIFT_ENTRANCE);
            ServerLevel entranceLevel = level.getServer().getLevel(entrance.dimension());
            if (entranceLevel != null) {
                target = entranceLevel;
                spot = entrance.pos();
            }
        }

        Vec3 arrival = NaturalRift.safeNear(target, spot);
        player.teleportTo(target, arrival.x, arrival.y, arrival.z, Set.of(), player.getYRot(), player.getXRot(), true);
        player.resetFallDistance();
        NaturalRift.blockTravel(player, level.getServer());
    }
}
