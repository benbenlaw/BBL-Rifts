package com.benbenlaw.rifts.network;

import com.benbenlaw.rifts.Rifts;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class RiftsNetworking {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(DisplacerSyncPayload.TYPE, DisplacerSyncPayload.STREAM_CODEC, DisplacerSyncPayload::handle)
                .playToServer(PortalGunFirePayload.TYPE, PortalGunFirePayload.STREAM_CODEC, PortalGunFirePayload::handle);
    }
}
