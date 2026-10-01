package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.EpochopolisBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyViews;
import com.benbenlaw.rifts.block.capability.RiftsCapabilities;
import com.benbenlaw.rifts.block.pipe.RiftPipeNetworks;
import com.benbenlaw.rifts.world.RiftEnergyData;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, EpochopolisBlockEntities.RIFT_PYLON_BLOCK_ENTITY.get(),
                (pylon, side) -> RiftEnergyViews.extractOnly(pylon.getRiftEnergyHandler()));

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, EpochopolisBlockEntities.RIFT_GENERATOR_BLOCK_ENTITY.get(),
                (generator, side) -> RiftEnergyViews.insertOnly(generator.getRiftEnergyHandler()));

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, EpochopolisBlockEntities.RIFT_INFUSER_BLOCK_ENTITY.get(),
                (infuser, side) -> RiftEnergyViews.insertOnly(infuser.getRiftEnergyHandler()));

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, EpochopolisBlockEntities.RIFT_STORAGE_BLOCK_ENTITY.get(),
                (storage, side) -> storage.getRiftEnergyHandler());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            RiftPipeNetworks.tick(serverLevel);
            if (serverLevel.getGameTime() % 100 == 0) {
                RiftEnergyData.get(serverLevel).tickDiffusion(serverLevel);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RiftPipeNetworks.clear();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        RiftPipeNetworks.clear();
    }
}
