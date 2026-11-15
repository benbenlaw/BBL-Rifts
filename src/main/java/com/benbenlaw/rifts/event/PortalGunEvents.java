package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.item.RiftPortalGunItem;
import com.benbenlaw.rifts.network.PortalGunFirePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class PortalGunEvents {

    private static boolean holdsGun(Player player) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RiftPortalGunItem;
    }

    @EventBusSubscriber(modid = Rifts.MOD_ID)
    public static class Common {

        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            if (!holdsGun(event.getEntity())) return;
            event.setCanceled(true);
            if (event.getEntity() instanceof ServerPlayer player) {
                RiftPortalGunItem.fire(player, player.getItemInHand(InteractionHand.MAIN_HAND), RiftPortalGunItem.SECOND_PORTAL);
            }
        }
    }

    @EventBusSubscriber(modid = Rifts.MOD_ID, value = Dist.CLIENT)
    public static class Client {

        @SubscribeEvent
        public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
            if (holdsGun(event.getEntity())) {
                ClientPacketDistributor.sendToServer(new PortalGunFirePayload());
            }
        }

        @SubscribeEvent
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            if (!event.getLevel().isClientSide() || !holdsGun(event.getEntity())) return;
            event.setCanceled(true);
            if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
                ClientPacketDistributor.sendToServer(new PortalGunFirePayload());
            }
        }
    }
}
