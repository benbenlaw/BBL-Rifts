package com.benbenlaw.rifts.util;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class EpochopolisTags {

    public static class Blocks {

        public static final TagKey<Block> SYNCED_ACROSS_TIMELINES = tag("synced_across_timelines");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(Rifts.identifier(name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", name));
        }
    }

}
