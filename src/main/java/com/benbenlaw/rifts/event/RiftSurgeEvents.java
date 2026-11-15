package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.surge.RiftSurgeManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class RiftSurgeEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RiftSurgeManager.onPlayerDeath(player);
        }
    }
}
