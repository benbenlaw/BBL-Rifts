package com.benbenlaw.rifts.network;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.item.RiftPortalGunItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record PortalGunFirePayload() implements CustomPacketPayload {

    public static final Type<PortalGunFirePayload> TYPE = new Type<>(Rifts.identifier("portal_gun_fire"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PortalGunFirePayload> STREAM_CODEC = StreamCodec.unit(new PortalGunFirePayload());

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PortalGunFirePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.getItem() instanceof RiftPortalGunItem) {
                RiftPortalGunItem.fire(player, stack, RiftPortalGunItem.SECOND_PORTAL);
            }
        }
    }
}
