package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.entity.RiftElemental;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(RiftsDataMaps.DISPLACER_HIT_RESULTS);
        event.register(RiftsDataMaps.BIOME_RIFT_ENERGY);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EpochopolisEntities.RIFT_ELEMENTAL.get(), RiftElemental.createAttributes().build());
    }
}
