package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.accelerator.TickAcceleration;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.block.capability.CreativeRiftEnergyHandler;
import com.benbenlaw.rifts.block.capability.RiftEnergyViews;
import com.benbenlaw.rifts.block.capability.RiftsCapabilities;
import com.benbenlaw.rifts.block.pipe.RiftPipeNetworks;
import com.benbenlaw.rifts.world.RiftEnergyData;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_PYLON_BLOCK_ENTITY.get(),
                (pylon, side) -> RiftEnergyViews.extractOnly(pylon.getRiftEnergyHandler()));

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_GENERATOR_BLOCK_ENTITY.get(),
                (generator, side) -> RiftEnergyViews.insertOnly(generator.getRiftEnergyHandler()));

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_INFUSER_BLOCK_ENTITY.get(),
                (infuser, side) -> RiftEnergyViews.insertOnly(infuser.getRiftEnergyHandler()));

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_FURNACE_BLOCK_ENTITY.get(),
                (furnace, side) -> RiftEnergyViews.insertOnly(furnace.getRiftEnergyHandler()));

        event.registerBlockEntity(Capabilities.Item.BLOCK, RiftsBlockEntities.RIFT_FURNACE_BLOCK_ENTITY.get(),
                (furnace, side) -> furnace.getItemHandler());

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_CRUSHER_BLOCK_ENTITY.get(),
                (crusher, side) -> RiftEnergyViews.insertOnly(crusher.getRiftEnergyHandler()));

        event.registerBlockEntity(Capabilities.Item.BLOCK, RiftsBlockEntities.RIFT_CRUSHER_BLOCK_ENTITY.get(),
                (crusher, side) -> crusher.getItemHandler());

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_TICK_ACCELERATOR_BLOCK_ENTITY.get(),
                (accelerator, side) -> RiftEnergyViews.insertOnly(accelerator.getRiftEnergyHandler()));

        event.registerBlock(RiftsCapabilities.RIFT_ENERGY,
                (level, pos, state, blockEntity, side) -> CreativeRiftEnergyHandler.INSTANCE,
                RiftsBlocks.CREATIVE_RIFT_STORAGE.get());

        event.registerBlockEntity(RiftsCapabilities.RIFT_ENERGY, RiftsBlockEntities.RIFT_STORAGE_BLOCK_ENTITY.get(),
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
        TickAcceleration.clear();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        RiftPipeNetworks.clear();
        TickAcceleration.clear();
    }
}
