package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.RiftEnergyModifier;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.block.RiftsBlocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RiftsDataMapProvider extends DataMapProvider {

    public RiftsDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(RiftsDataMaps.DISPLACER_HIT_RESULTS)
                .add(RiftsItems.DISPLACER.getKey(),
                        new DisplacerConversions(3, 0.75F, Map.of(
                                Blocks.STONE, Blocks.DIAMOND_ORE,
                                Blocks.DEEPSLATE, Blocks.DEEPSLATE_DIAMOND_ORE
                        ), Map.of(
                                BlockTags.LOGS, RiftsBlocks.RIFT_LOG.get()
                        ), Map.of(
                                EntityType.IRON_GOLEM, EpochopolisEntities.RIFT_ELEMENTAL.get()
                        )),
                        false);

        // Rift energy richness per biome. The rift dimension is far richer than anywhere else
        builder(RiftsDataMaps.BIOME_RIFT_ENERGY)
                .add(RiftsWorldGen.RIFT_BIOME, new RiftEnergyModifier(4.0F), false)
                .add(Biomes.DEEP_DARK, new RiftEnergyModifier(2.0F), false)
                .add(Biomes.SOUL_SAND_VALLEY, new RiftEnergyModifier(1.5F), false)
                .add(Biomes.THE_END, new RiftEnergyModifier(1.5F), false)
                .add(Biomes.MUSHROOM_FIELDS, new RiftEnergyModifier(1.25F), false)
                .add(Biomes.DESERT, new RiftEnergyModifier(0.75F), false)
                .add(Biomes.PLAINS, new RiftEnergyModifier(0.9F), false);
    }
}