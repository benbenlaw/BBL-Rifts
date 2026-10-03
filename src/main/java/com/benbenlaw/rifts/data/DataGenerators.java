package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new RiftsDataMapProvider(packOutput, lookupProvider));
        generator.addProvider(true, new RiftsLangProvider(packOutput));
        event.createDatapackRegistryObjects(RiftsWorldGen.BUILDER);
        generator.addProvider(true, new RiftsModelProvider(packOutput));
        generator.addProvider(true, new RiftsEquipmentAssetProvider(packOutput));
        generator.addProvider(true, new RiftsEntityLootTableProvider(packOutput, lookupProvider));
        generator.addProvider(true, new RiftsBlockTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new RiftsItemTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new RiftsRecipesProvider.Runner(packOutput, lookupProvider));
    }
}
