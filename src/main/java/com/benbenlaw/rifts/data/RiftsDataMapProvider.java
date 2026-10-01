package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import net.minecraft.world.entity.EntityType;
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
                                EntityType.IRON_GOLEM, EpochopolisEntities.RIFT_ELEMENTAL.get()
                        )),
                        false);
    }
}