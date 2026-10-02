package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.util.EpochopolisTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class RiftsBlockTagsProvider extends BlockTagsProvider {

    public RiftsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Rifts.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EpochopolisTags.Blocks.PYLONS).add(
                RiftsBlocks.BASIC_RIFT_PYLON.get(),
                RiftsBlocks.ADVANCED_RIFT_PYLON.get(),
                RiftsBlocks.ELITE_RIFT_PYLON.get(),
                RiftsBlocks.ULTIMATE_RIFT_PYLON.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(EpochopolisTags.Blocks.PYLONS)
                .add(RiftsBlocks.RIFT_GENERATOR.get(),
                        RiftsBlocks.RIFT_INFUSER.get(),
                        RiftsBlocks.RIFT_STORAGE.get(),
                        RiftsBlocks.RIFT_PIPE.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        RiftsBlocks.RIFT_LOG.get(),
                        RiftsBlocks.RIFT_PLANKS.get(),
                        RiftsBlocks.RIFT_PLANK_SLAB.get(),
                        RiftsBlocks.RIFT_PLANK_STAIRS.get()
                )
        ;

        tag(BlockTags.LOGS).add(RiftsBlocks.RIFT_LOG.get());
        tag(BlockTags.PLANKS).add(RiftsBlocks.RIFT_PLANKS.get());
        tag(BlockTags.WOODEN_STAIRS).add(RiftsBlocks.RIFT_PLANK_STAIRS.get());
        tag(BlockTags.WOODEN_SLABS).add(RiftsBlocks.RIFT_PLANK_SLAB.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(EpochopolisTags.Blocks.PYLONS)
                .add(RiftsBlocks.RIFT_GENERATOR.get(),
                        RiftsBlocks.RIFT_INFUSER.get(),
                        RiftsBlocks.RIFT_STORAGE.get());
    }
}
