package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RiftsLangProvider extends LanguageProvider {

    private static final String ACCELERATOR_TOOLTIP = "Gives each adjacent machine or crop %1$s extra ticks every tick, using %2$s rift energy per tick while it is working. Needs a rift pipe for energy.";

    private static final String PYLON_TOOLTIP = "Stores %1$s rift energy and draws %2$s per second from the chunk it is in.";

    public RiftsLangProvider(PackOutput output) {
        super(output, Rifts.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + Rifts.MOD_ID, "Rifts");

        addDimension(RiftsWorldGen.RIFT_LEVEL, "The Rift");
        addBiome(RiftsWorldGen.RIFT_BIOME, "Rift Wastes");

        addBlock(RiftsBlocks.RIFT_GENERATOR, "Rift Generator");
        addBlock(RiftsBlocks.RIFT_INFUSER, "Rift Infuser");
        addBlock(RiftsBlocks.RIFT_CHARGER, "Rift Charger");
        addBlock(RiftsBlocks.RIFT_CRUSHER, "Rift Crusher");
        addBlock(RiftsBlocks.RIFT_FURNACE, "Rift Furnace");
        addBlock(RiftsBlocks.BASIC_TICK_ACCELERATOR, "Basic Tick Accelerator");
        addBlock(RiftsBlocks.ADVANCED_TICK_ACCELERATOR, "Advanced Tick Accelerator");
        addBlock(RiftsBlocks.ELITE_TICK_ACCELERATOR, "Elite Tick Accelerator");
        addBlock(RiftsBlocks.ULTIMATE_TICK_ACCELERATOR, "Ultimate Tick Accelerator");
        addBlock(RiftsBlocks.CREATIVE_RIFT_STORAGE, "Creative Rift Storage");
        addBlock(RiftsBlocks.RIFT_STORAGE, "Rift Storage");
        addBlock(RiftsBlocks.RIFT_PIPE, "Rift Pipe");
        addBlock(RiftsBlocks.BASIC_RIFT_PYLON, "Basic Rift Pylon");
        addBlock(RiftsBlocks.ADVANCED_RIFT_PYLON, "Advanced Rift Pylon");
        addBlock(RiftsBlocks.ELITE_RIFT_PYLON, "Elite Rift Pylon");
        addBlock(RiftsBlocks.ULTIMATE_RIFT_PYLON, "Ultimate Rift Pylon");

        addBlock(RiftsBlocks.RIFT_STONE, "Rift Stone");
        addBlock(RiftsBlocks.RIFT_LEAVES, "Rift Leaves");
        addBlock(RiftsBlocks.RIFT_LOG, "Rift Log");
        addBlock(RiftsBlocks.RIFT_PLANKS, "Rift Planks");
        addBlock(RiftsBlocks.RIFT_PLANK_STAIRS, "Rift Plank Stairs");
        addBlock(RiftsBlocks.RIFT_PLANK_SLAB, "Rift Plank Slab");

        addItem(RiftsItems.DISPLACER, "Displacer");
        addItem(RiftsItems.RIFT_SCANNER, "Rift Scanner");
        addItem(RiftsItems.RIFT_WRENCH, "Rift Wrench");
        addItem(RiftsItems.RIFT_STEEL_INGOT, "Rift Steel Ingot");
        addItem(RiftsItems.RIFT_STEEL_SWORD, "Rift Steel Sword");
        addItem(RiftsItems.RIFT_STEEL_PICKAXE, "Rift Steel Pickaxe");
        addItem(RiftsItems.RIFT_STEEL_AXE, "Rift Steel Axe");
        addItem(RiftsItems.RIFT_STEEL_SHOVEL, "Rift Steel Shovel");
        addItem(RiftsItems.RIFT_STEEL_HOE, "Rift Steel Hoe");
        addItem(RiftsItems.RIFT_STEEL_SPEAR, "Rift Steel Spear");
        addItem(RiftsItems.RIFT_STEEL_HELMET, "Rift Steel Helmet");
        addItem(RiftsItems.RIFT_STEEL_CHESTPLATE, "Rift Steel Chestplate");
        addItem(RiftsItems.RIFT_STEEL_LEGGINGS, "Rift Steel Leggings");
        addItem(RiftsItems.RIFT_STEEL_BOOTS, "Rift Steel Boots");
        addItem(RiftsItems.RIFT_ELEMENTAL_SPAWN_EGG, "Rift Elemental Spawn Egg");
        addEntityType(EpochopolisEntities.DISPLACER, "Displacer");
        addEntityType(EpochopolisEntities.RIFT_ELEMENTAL, "Rift Elemental");
        addEntityType(EpochopolisEntities.NATURAL_RIFT, "Rift");

        add("tooltip.rifts.stored_energy", "Stored Rift Energy: %1$s");
        add("tooltip.rifts.item_energy", "Rift Energy: %1$s / %2$s");

        add("jei.rifts.displacer_conversions", "Displacer Conversions");
        add("jei.rifts.crusher", "Crushing");
        add("jei.rifts.crusher.bonus_chance", "Bonus output: %1$s%% chance");

        add("tooltip.rifts.basic_tick_accelerator", ACCELERATOR_TOOLTIP);
        add("tooltip.rifts.advanced_tick_accelerator", ACCELERATOR_TOOLTIP);
        add("tooltip.rifts.elite_tick_accelerator", ACCELERATOR_TOOLTIP);
        add("tooltip.rifts.ultimate_tick_accelerator", ACCELERATOR_TOOLTIP);

        add("tooltip.rifts.basic_rift_pylon", PYLON_TOOLTIP);
        add("tooltip.rifts.advanced_rift_pylon", PYLON_TOOLTIP);
        add("tooltip.rifts.elite_rift_pylon", PYLON_TOOLTIP);
        add("tooltip.rifts.ultimate_rift_pylon", PYLON_TOOLTIP);

        add("message.rifts.rift_scanner", "Rift Energy: %1$s / %2$s (%3$s%%)  |  Richness x%4$s");
        add("message.rifts.pipe_side", "%1$s side: %2$s");
        add("message.rifts.pipe_no_connection", "Nothing connected on that side");
        add("message.rifts.pipe_mode.insert", "Insert");
        add("message.rifts.pipe_mode.extract", "Extract");
        add("message.rifts.pipe_mode.off", "Off");

        add("direction.rifts.north", "North");
        add("direction.rifts.south", "South");
        add("direction.rifts.east", "East");
        add("direction.rifts.west", "West");
        add("direction.rifts.up", "Top");
        add("direction.rifts.down", "Bottom");
    }
}
