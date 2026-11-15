package com.benbenlaw.rifts.network;

import io.netty.buffer.ByteBuf;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.datamaps.ClientDisplacerData;
import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public record DisplacerSyncPayload(Map<ResourceKey<Level>, DisplacerConversions> data) implements CustomPacketPayload {

    public static final Type<DisplacerSyncPayload> TYPE = new Type<>(Rifts.identifier("displacer_sync"));

    public static final StreamCodec<ByteBuf, DisplacerSyncPayload> STREAM_CODEC =
            ByteBufCodecs.<ByteBuf, ResourceKey<Level>, DisplacerConversions, Map<ResourceKey<Level>, DisplacerConversions>>map(
                    HashMap::new, ResourceKey.streamCodec(Registries.DIMENSION), ByteBufCodecs.fromCodec(DisplacerConversions.CODEC))
                    .map(DisplacerSyncPayload::new, DisplacerSyncPayload::data);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DisplacerSyncPayload payload, IPayloadContext context) {
        ClientDisplacerData.set(payload.data());
    }
}
