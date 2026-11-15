package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.network.DisplacerSyncPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import com.benbenlaw.rifts.entity.RiftsEntities;
import com.benbenlaw.rifts.entity.RiftElemental;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import net.neoforged.bus.api.SubscribeEvent;
import com.benbenlaw.rifts.block.RiftsBlocks;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
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
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RiftsBlocks::registerFlammability);
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        DisplacerSyncPayload payload = new DisplacerSyncPayload(DisplacerConversions.all(event.getPlayerList().getServer().registryAccess()));
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));

        event.sendRecipes(RiftsRecipeTypes.CRUSHER_TYPE.get());
        event.sendRecipes(RiftsRecipeTypes.INFUSER_TYPE.get());
        event.sendRecipes(RiftsRecipeTypes.QUARRY_TYPE.get());
        event.sendRecipes(RiftsRecipeTypes.CONVERTER_TYPE.get());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(RiftsEntities.RIFT_ELEMENTAL.get(), RiftElemental.createAttributes().build());
    }
}
