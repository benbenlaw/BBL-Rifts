package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.entity.RiftsAttachments;
import com.benbenlaw.rifts.item.energy.RiftEnergyItem;
import com.benbenlaw.rifts.util.RiftsTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class RiftFlightEvents {

    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        Abilities abilities = player.getAbilities();
        boolean eligible = player.level().dimension().equals(RiftsWorldGen.RIFT_LEVEL) && wearsPoweredSuit(player);

        if (eligible) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                player.setData(RiftsAttachments.RIFT_FLIGHT_GRANTED, true);
                player.onUpdateAbilities();
            }
            if (abilities.flying) {
                drain(player);
            }
        } else if (player.getData(RiftsAttachments.RIFT_FLIGHT_GRANTED)) {
            abilities.mayfly = false;
            abilities.flying = false;
            player.setData(RiftsAttachments.RIFT_FLIGHT_GRANTED, false);
            player.onUpdateAbilities();
        }
    }

    private static boolean wearsPoweredSuit(ServerPlayer player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.is(RiftsTags.Items.RIFT_PROTECTIVE_ARMOR) || !(stack.getItem() instanceof RiftEnergyItem)
                    || RiftEnergyItem.getEnergy(stack) <= 0) {
                return false;
            }
        }
        return true;
    }

    private static void drain(ServerPlayer player) {
        int cost = RiftsStartupConfig.RIFT_FLIGHT_ENERGY_PER_TICK.get();
        if (cost <= 0) return;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            RiftEnergyItem.setEnergy(stack, Math.max(0, RiftEnergyItem.getEnergy(stack) - cost));
        }
    }
}
