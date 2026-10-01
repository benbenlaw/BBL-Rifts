package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.util.EpochopolisTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class RiftsItemTagsProvider extends ItemTagsProvider {

    public RiftsItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Rifts.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EpochopolisTags.Items.PYLONS).add(
                RiftsBlocks.BASIC_RIFT_PYLON.asItem(),
                RiftsBlocks.ADVANCED_RIFT_PYLON.asItem(),
                RiftsBlocks.ELITE_RIFT_PYLON.asItem(),
                RiftsBlocks.ULTIMATE_RIFT_PYLON.asItem());
    }
}
