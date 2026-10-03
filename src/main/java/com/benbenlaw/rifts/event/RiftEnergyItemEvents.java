package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.item.energy.RiftEnergyItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class RiftEnergyItemEvents {

    @SubscribeEvent
    public static void onAttributeModifiers(ItemAttributeModifierEvent event) {
        if (RiftEnergyItem.isEmpty(event.getItemStack())) {
            event.clearModifiers();
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (RiftEnergyItem.isEmpty(event.getEntity().getMainHandItem())) {
            event.setNewSpeed(Math.min(event.getNewSpeed(), 1.0F));
        }
    }
}
