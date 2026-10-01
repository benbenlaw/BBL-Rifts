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

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(Rifts.identifier(name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {

        public static final TagKey<Item> PYLONS = ItemTags.create(Rifts.identifier("pylons"));
    }

}
