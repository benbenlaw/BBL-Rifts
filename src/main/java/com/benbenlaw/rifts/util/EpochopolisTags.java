package com.benbenlaw.rifts.util;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EpochopolisTags {

    public static class Blocks {

        public static final TagKey<Block> SYNCED_ACROSS_TIMELINES = tag("synced_across_timelines");
        public static final TagKey<Block> PYLONS = tag("pylons");
        public static final TagKey<Block> TICK_ACCELERATOR_BLACKLIST = tag("tick_accelerator_blacklist");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(Rifts.identifier(name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {

        public static final TagKey<Item> PYLONS = ItemTags.create(Rifts.identifier("pylons"));
        public static final TagKey<Item> RIFT_STEEL_TOOL_MATERIALS = ItemTags.create(Rifts.identifier("rift_steel_tool_materials"));
        public static final TagKey<Item> RIFT_PROTECTIVE_ARMOR = ItemTags.create(Rifts.identifier("rift_protective_armor"));
        public static final TagKey<Item> REPAIRS_RIFT_STEEL_ARMOR = ItemTags.create(Rifts.identifier("repairs_rift_steel_armor"));
    }

}
