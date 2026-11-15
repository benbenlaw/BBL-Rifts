package com.benbenlaw.rifts.data;

import java.util.stream.Stream;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.util.RiftsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class RiftsBlockTagsProvider extends BlockTagsProvider {

    public RiftsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Rifts.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(RiftsTags.Blocks.PYLONS).add(
                RiftsBlocks.BASIC_RIFT_PYLON.get(),
                RiftsBlocks.ADVANCED_RIFT_PYLON.get(),
                RiftsBlocks.ELITE_RIFT_PYLON.get(),
                RiftsBlocks.ULTIMATE_RIFT_PYLON.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(RiftsTags.Blocks.PYLONS)
                .add(RiftsBlocks.RIFT_GENERATOR.get(),
                        RiftsBlocks.RIFT_INFUSER.get(),
                        RiftsBlocks.RIFT_STORAGE.get(),
                        RiftsBlocks.RIFT_PIPE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(RiftsBlocks.RIFT_CHARGER.get(), RiftsBlocks.RIFT_QUARRY.get(), RiftsBlocks.RIFT_CONVERTER.get(), RiftsBlocks.RIFT_SURGE_CORE.get());

        tag(BlockTags.NEEDS_STONE_TOOL).add(RiftsBlocks.RIFT_CHARGER.get(), RiftsBlocks.RIFT_QUARRY.get(), RiftsBlocks.RIFT_CONVERTER.get(), RiftsBlocks.RIFT_SURGE_CORE.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        RiftsBlocks.RIFT_LOG.get(),
                        RiftsBlocks.RIFT_PLANKS.get(),
                        RiftsBlocks.RIFT_PLANK_SLAB.get(),
                        RiftsBlocks.RIFT_PLANK_STAIRS.get()
                )
        ;

        tag(RiftsTags.Blocks.RIFT_STEEL_STORAGE_BLOCKS).add(RiftsBlocks.RIFT_STEEL_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(RiftsTags.Blocks.RIFT_STEEL_STORAGE_BLOCKS);
        tag(BlockTags.LOGS).add(RiftsBlocks.RIFT_LOG.get());
        tag(BlockTags.LEAVES).add(RiftsBlocks.RIFT_LEAVES.get());
        tag(BlockTags.MINEABLE_WITH_HOE).add(RiftsBlocks.RIFT_LEAVES.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(RiftsBlocks.RIFT_STONE.get());
        tag(BlockTags.PLANKS).add(RiftsBlocks.RIFT_PLANKS.get());
        tag(BlockTags.WOODEN_STAIRS).add(RiftsBlocks.RIFT_PLANK_STAIRS.get());
        tag(BlockTags.WOODEN_SLABS).add(RiftsBlocks.RIFT_PLANK_SLAB.get());

        tag(BlockTags.LOGS).add(RiftsBlocks.RIFT_WOOD.get(), RiftsBlocks.STRIPPED_RIFT_LOG.get(), RiftsBlocks.STRIPPED_RIFT_WOOD.get());
        tag(BlockTags.WOODEN_FENCES).add(RiftsBlocks.RIFT_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(RiftsBlocks.RIFT_FENCE_GATE.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(RiftsBlocks.RIFT_TRAPDOOR.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(RiftsBlocks.RIFT_PRESSURE_PLATE.get());
        tag(BlockTags.WOODEN_DOORS).add(RiftsBlocks.RIFT_DOOR.get());
        tag(BlockTags.WOODEN_BUTTONS).add(RiftsBlocks.RIFT_BUTTON.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(
                RiftsBlocks.RIFT_WOOD.get(), RiftsBlocks.STRIPPED_RIFT_LOG.get(), RiftsBlocks.STRIPPED_RIFT_WOOD.get(),
                RiftsBlocks.RIFT_FENCE.get(), RiftsBlocks.RIFT_FENCE_GATE.get(), RiftsBlocks.RIFT_TRAPDOOR.get(),
                RiftsBlocks.RIFT_PRESSURE_PLATE.get(), RiftsBlocks.RIFT_DOOR.get(), RiftsBlocks.RIFT_BUTTON.get());

        tag(BlockTags.STANDING_SIGNS).add(RiftsBlocks.RIFT_SIGN.get());
        tag(BlockTags.WALL_SIGNS).add(RiftsBlocks.RIFT_WALL_SIGN.get());
        tag(BlockTags.CEILING_HANGING_SIGNS).add(RiftsBlocks.RIFT_HANGING_SIGN.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(RiftsBlocks.RIFT_WALL_HANGING_SIGN.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(RiftsBlocks.RIFT_SIGN.get(), RiftsBlocks.RIFT_WALL_SIGN.get(), RiftsBlocks.RIFT_HANGING_SIGN.get(), RiftsBlocks.RIFT_WALL_HANGING_SIGN.get());
        tag(BlockTags.LOGS_THAT_BURN).add(RiftsBlocks.RIFT_LOG.get(), RiftsBlocks.RIFT_WOOD.get(), RiftsBlocks.STRIPPED_RIFT_LOG.get(), RiftsBlocks.STRIPPED_RIFT_WOOD.get());

        tag(BlockTags.STONE_BUTTONS).add(RiftsBlocks.RIFT_STONE_BUTTON.get());
        tag(BlockTags.STONE_PRESSURE_PLATES).add(RiftsBlocks.RIFT_STONE_PRESSURE_PLATE.get());
        tag(BlockTags.STAIRS).add(RiftsBlocks.RIFT_STONE_STAIRS.get(), RiftsBlocks.POLISHED_RIFT_STONE_STAIRS.get(), RiftsBlocks.RIFT_STONE_BRICK_STAIRS.get());
        tag(BlockTags.SLABS).add(RiftsBlocks.RIFT_STONE_SLAB.get(), RiftsBlocks.POLISHED_RIFT_STONE_SLAB.get(), RiftsBlocks.RIFT_STONE_BRICK_SLAB.get());
        tag(BlockTags.WALLS).add(RiftsBlocks.RIFT_STONE_WALL.get(), RiftsBlocks.POLISHED_RIFT_STONE_WALL.get(), RiftsBlocks.RIFT_STONE_BRICK_WALL.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                RiftsBlocks.RIFT_STONE_STAIRS.get(), RiftsBlocks.RIFT_STONE_SLAB.get(), RiftsBlocks.RIFT_STONE_WALL.get(),
                RiftsBlocks.RIFT_STONE_BUTTON.get(), RiftsBlocks.RIFT_STONE_PRESSURE_PLATE.get(),
                RiftsBlocks.POLISHED_RIFT_STONE.get(), RiftsBlocks.POLISHED_RIFT_STONE_STAIRS.get(),
                RiftsBlocks.POLISHED_RIFT_STONE_SLAB.get(), RiftsBlocks.POLISHED_RIFT_STONE_WALL.get(),
                RiftsBlocks.RIFT_STONE_BRICKS.get(), RiftsBlocks.RIFT_STONE_BRICK_STAIRS.get(),
                RiftsBlocks.RIFT_STONE_BRICK_SLAB.get(), RiftsBlocks.RIFT_STONE_BRICK_WALL.get(),
                RiftsBlocks.CRACKED_RIFT_STONE_BRICKS.get(), RiftsBlocks.CHISELED_RIFT_STONE_BRICKS.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                        RiftsBlocks.BASIC_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ADVANCED_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ELITE_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ULTIMATE_TICK_ACCELERATOR.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(RiftsBlocks.CREATIVE_RIFT_STORAGE.get());

        tag(BlockTags.NEEDS_STONE_TOOL).add(
                        RiftsBlocks.BASIC_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ADVANCED_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ELITE_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ULTIMATE_TICK_ACCELERATOR.get());

        tag(RiftsTags.Blocks.TICK_ACCELERATOR_BLACKLIST)
                .addTag(RiftsTags.Blocks.PYLONS)
                .add(RiftsBlocks.RIFT_GENERATOR.get(),
                        RiftsBlocks.BASIC_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ADVANCED_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ELITE_TICK_ACCELERATOR.get(),
                        RiftsBlocks.ULTIMATE_TICK_ACCELERATOR.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(RiftsTags.Blocks.PYLONS)
                .add(RiftsBlocks.RIFT_GENERATOR.get(),
                        RiftsBlocks.RIFT_INFUSER.get(),
                        RiftsBlocks.RIFT_STORAGE.get());

        for (var block : Stream.concat(RiftsBlocks.RIFT_CRUSHERS.stream(), RiftsBlocks.RIFT_FURNACES.stream()).toList()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
            tag(BlockTags.NEEDS_STONE_TOOL).add(block.get());
        }
    }
}
