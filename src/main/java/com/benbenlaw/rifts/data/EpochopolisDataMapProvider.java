package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.EpochopolisDataMaps;
import com.benbenlaw.rifts.item.EpochopolisItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EpochopolisDataMapProvider extends DataMapProvider {

    public EpochopolisDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(EpochopolisDataMaps.DISPLACER_HIT_RESULTS)
                .add(EpochopolisItems.DISPLACER.getKey(),
                        new DisplacerConversions(3, 0.75F, Map.of(
                                Blocks.STONE, Blocks.DIAMOND_ORE,
                                Blocks.DEEPSLATE, Blocks.DEEPSLATE_DIAMOND_ORE
                        )),
                        false);
    }
}