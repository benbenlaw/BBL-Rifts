package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.block.RiftsBlocks;
import net.minecraft.data.BlockFamily;

import java.util.List;

public class RiftsBlockFamilies {

    public static final BlockFamily RIFT_PLANKS = new BlockFamily.Builder(RiftsBlocks.RIFT_PLANKS.get())
            .button(RiftsBlocks.RIFT_BUTTON.get())
            .fence(RiftsBlocks.RIFT_FENCE.get())
            .fenceGate(RiftsBlocks.RIFT_FENCE_GATE.get())
            .pressurePlate(RiftsBlocks.RIFT_PRESSURE_PLATE.get())
            .slab(RiftsBlocks.RIFT_PLANK_SLAB.get())
            .stairs(RiftsBlocks.RIFT_PLANK_STAIRS.get())
            .door(RiftsBlocks.RIFT_DOOR.get())
            .trapdoor(RiftsBlocks.RIFT_TRAPDOOR.get())
            .sign(RiftsBlocks.RIFT_SIGN.get(), RiftsBlocks.RIFT_WALL_SIGN.get())
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static final BlockFamily RIFT_STONE = new BlockFamily.Builder(RiftsBlocks.RIFT_STONE.get())
            .button(RiftsBlocks.RIFT_STONE_BUTTON.get())
            .pressurePlate(RiftsBlocks.RIFT_STONE_PRESSURE_PLATE.get())
            .slab(RiftsBlocks.RIFT_STONE_SLAB.get())
            .stairs(RiftsBlocks.RIFT_STONE_STAIRS.get())
            .wall(RiftsBlocks.RIFT_STONE_WALL.get())
            .getFamily();

    public static final BlockFamily POLISHED_RIFT_STONE = new BlockFamily.Builder(RiftsBlocks.POLISHED_RIFT_STONE.get())
            .slab(RiftsBlocks.POLISHED_RIFT_STONE_SLAB.get())
            .stairs(RiftsBlocks.POLISHED_RIFT_STONE_STAIRS.get())
            .wall(RiftsBlocks.POLISHED_RIFT_STONE_WALL.get())
            .getFamily();

    public static final BlockFamily RIFT_STONE_BRICKS = new BlockFamily.Builder(RiftsBlocks.RIFT_STONE_BRICKS.get())
            .slab(RiftsBlocks.RIFT_STONE_BRICK_SLAB.get())
            .stairs(RiftsBlocks.RIFT_STONE_BRICK_STAIRS.get())
            .wall(RiftsBlocks.RIFT_STONE_BRICK_WALL.get())
            .cracked(RiftsBlocks.CRACKED_RIFT_STONE_BRICKS.get())
            .chiseled(RiftsBlocks.CHISELED_RIFT_STONE_BRICKS.get())
            .getFamily();

    public static final List<BlockFamily> ALL = List.of(RIFT_PLANKS, RIFT_STONE, POLISHED_RIFT_STONE, RIFT_STONE_BRICKS);
}
