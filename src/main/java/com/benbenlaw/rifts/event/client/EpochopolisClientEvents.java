package com.benbenlaw.rifts.event.client;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID, value = Dist.CLIENT)
public class EpochopolisClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EpochopolisEntities.DISPLACER.get(), ThrownItemRenderer::new);
    }
}