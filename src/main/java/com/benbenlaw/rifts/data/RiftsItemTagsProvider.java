package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.item.RiftsItems;
import com.benbenlaw.rifts.util.RiftsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class RiftsItemTagsProvider extends ItemTagsProvider {

    public RiftsItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Rifts.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(RiftsTags.Items.PYLONS).add(
                RiftsBlocks.BASIC_RIFT_PYLON.asItem(),
                RiftsBlocks.ADVANCED_RIFT_PYLON.asItem(),
                RiftsBlocks.ELITE_RIFT_PYLON.asItem(),
                RiftsBlocks.ULTIMATE_RIFT_PYLON.asItem());

        tag(RiftsTags.Items.RIFT_STEEL_TOOL_MATERIALS).add(RiftsItems.RIFT_STEEL_INGOT.get());
        tag(RiftsTags.Items.REPAIRS_RIFT_STEEL_ARMOR).add(RiftsItems.RIFT_STEEL_INGOT.get());

        tag(RiftsTags.Items.RIFT_STEEL_INGOTS).add(RiftsItems.RIFT_STEEL_INGOT.get());
        tag(RiftsTags.Items.RIFT_STEEL_NUGGETS).add(RiftsItems.RIFT_STEEL_NUGGET.get());
        tag(RiftsTags.Items.RIFT_STEEL_STORAGE_BLOCKS).add(RiftsBlocks.RIFT_STEEL_BLOCK.asItem());
        tag(Tags.Items.INGOTS).addTag(RiftsTags.Items.RIFT_STEEL_INGOTS);
        tag(Tags.Items.NUGGETS).addTag(RiftsTags.Items.RIFT_STEEL_NUGGETS);
        tag(Tags.Items.STORAGE_BLOCKS).addTag(RiftsTags.Items.RIFT_STEEL_STORAGE_BLOCKS);

        tag(Tags.Items.TOOLS_WRENCH).add(RiftsItems.RIFT_WRENCH.get());

        tag(RiftsTags.Items.RIFT_PROTECTIVE_ARMOR).add(
                RiftsItems.RIFT_STEEL_HELMET.get(),
                RiftsItems.RIFT_STEEL_CHESTPLATE.get(),
                RiftsItems.RIFT_STEEL_LEGGINGS.get(),
                RiftsItems.RIFT_STEEL_BOOTS.get());

        tag(ItemTags.SWORDS).add(RiftsItems.RIFT_STEEL_SWORD.get());
        tag(ItemTags.PICKAXES).add(RiftsItems.RIFT_STEEL_PICKAXE.get());
        tag(ItemTags.AXES).add(RiftsItems.RIFT_STEEL_AXE.get());
        tag(ItemTags.SHOVELS).add(RiftsItems.RIFT_STEEL_SHOVEL.get());
        tag(ItemTags.HOES).add(RiftsItems.RIFT_STEEL_HOE.get());
        tag(ItemTags.SPEARS).add(RiftsItems.RIFT_STEEL_SPEAR.get());
        tag(ItemTags.HEAD_ARMOR).add(RiftsItems.RIFT_STEEL_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(RiftsItems.RIFT_STEEL_CHESTPLATE.get());
        tag(ItemTags.LEG_ARMOR).add(RiftsItems.RIFT_STEEL_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(RiftsItems.RIFT_STEEL_BOOTS.get());

        tag(ItemTags.LOGS).add(RiftsBlocks.RIFT_LOG.get().asItem());
        tag(ItemTags.LEAVES).add(RiftsBlocks.RIFT_LEAVES.get().asItem());
        tag(ItemTags.PLANKS).add(RiftsBlocks.RIFT_PLANKS.get().asItem());
        tag(ItemTags.WOODEN_STAIRS).add(RiftsBlocks.RIFT_PLANK_STAIRS.get().asItem());
        tag(ItemTags.WOODEN_SLABS).add(RiftsBlocks.RIFT_PLANK_SLAB.get().asItem());

        tag(ItemTags.LOGS).add(RiftsBlocks.RIFT_WOOD.get().asItem(), RiftsBlocks.STRIPPED_RIFT_LOG.get().asItem(), RiftsBlocks.STRIPPED_RIFT_WOOD.get().asItem());
        tag(ItemTags.WOODEN_FENCES).add(RiftsBlocks.RIFT_FENCE.get().asItem());
        tag(ItemTags.FENCE_GATES).add(RiftsBlocks.RIFT_FENCE_GATE.get().asItem());
        tag(ItemTags.WOODEN_TRAPDOORS).add(RiftsBlocks.RIFT_TRAPDOOR.get().asItem());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(RiftsBlocks.RIFT_PRESSURE_PLATE.get().asItem());
        tag(ItemTags.WOODEN_DOORS).add(RiftsBlocks.RIFT_DOOR.get().asItem());
        tag(ItemTags.WOODEN_BUTTONS).add(RiftsBlocks.RIFT_BUTTON.get().asItem());

        tag(ItemTags.SIGNS).add(RiftsBlocks.RIFT_SIGN.get().asItem());
        tag(ItemTags.HANGING_SIGNS).add(RiftsBlocks.RIFT_HANGING_SIGN.get().asItem());
        tag(ItemTags.LOGS_THAT_BURN).add(RiftsBlocks.RIFT_LOG.get().asItem(), RiftsBlocks.RIFT_WOOD.get().asItem(), RiftsBlocks.STRIPPED_RIFT_LOG.get().asItem(), RiftsBlocks.STRIPPED_RIFT_WOOD.get().asItem());

        tag(ItemTags.STONE_BUTTONS).add(RiftsBlocks.RIFT_STONE_BUTTON.get().asItem());
        tag(ItemTags.STAIRS).add(RiftsBlocks.RIFT_STONE_STAIRS.get().asItem(), RiftsBlocks.POLISHED_RIFT_STONE_STAIRS.get().asItem(), RiftsBlocks.RIFT_STONE_BRICK_STAIRS.get().asItem());
        tag(ItemTags.SLABS).add(RiftsBlocks.RIFT_STONE_SLAB.get().asItem(), RiftsBlocks.POLISHED_RIFT_STONE_SLAB.get().asItem(), RiftsBlocks.RIFT_STONE_BRICK_SLAB.get().asItem());
        tag(ItemTags.WALLS).add(RiftsBlocks.RIFT_STONE_WALL.get().asItem(), RiftsBlocks.POLISHED_RIFT_STONE_WALL.get().asItem(), RiftsBlocks.RIFT_STONE_BRICK_WALL.get().asItem());


    }




}
