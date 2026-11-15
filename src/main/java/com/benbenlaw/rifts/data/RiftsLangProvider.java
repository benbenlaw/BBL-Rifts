package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.entity.RiftsEntities;
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

        addBlock(RiftsBlocks.RIFT_GENERATOR, "Rift Portal Generator");
        addBlock(RiftsBlocks.RIFT_INFUSER, "Rift Energy Infuser");
        addBlock(RiftsBlocks.RIFT_CHARGER, "Rift Energy Charger");
        addBlock(RiftsBlocks.BASIC_RIFT_CRUSHER, "Basic Rift Powered Crusher");
        addBlock(RiftsBlocks.ADVANCED_RIFT_CRUSHER, "Advanced Rift Powered Crusher");
        addBlock(RiftsBlocks.ELITE_RIFT_CRUSHER, "Elite Rift Powered Crusher");
        addBlock(RiftsBlocks.ULTIMATE_RIFT_CRUSHER, "Ultimate Rift Powered Crusher");
        addBlock(RiftsBlocks.RIFT_QUARRY, "Rift Powered Quarry");
        addBlock(RiftsBlocks.RIFT_SURGE_CORE, "Rift Surger");
        addBlock(RiftsBlocks.RIFT_CONVERTER, "Rift Converter");
        addBlock(RiftsBlocks.BASIC_RIFT_FURNACE, "Basic Rift Furnace");
        addBlock(RiftsBlocks.ADVANCED_RIFT_FURNACE, "Advanced Rift Furnace");
        addBlock(RiftsBlocks.ELITE_RIFT_FURNACE, "Elite Rift Furnace");
        addBlock(RiftsBlocks.ULTIMATE_RIFT_FURNACE, "Ultimate Rift Furnace");
        addBlock(RiftsBlocks.BASIC_TICK_ACCELERATOR, "Basic Tick Accelerator");
        addBlock(RiftsBlocks.ADVANCED_TICK_ACCELERATOR, "Advanced Tick Accelerator");
        addBlock(RiftsBlocks.ELITE_TICK_ACCELERATOR, "Elite Tick Accelerator");
        addBlock(RiftsBlocks.ULTIMATE_TICK_ACCELERATOR, "Ultimate Tick Accelerator");
        addBlock(RiftsBlocks.CREATIVE_RIFT_STORAGE, "Creative Rift Storage");
        addBlock(RiftsBlocks.RIFT_STORAGE, "Rift Storage");
        addBlock(RiftsBlocks.RIFT_PIPE, "Rift Pipe");
        addBlock(RiftsBlocks.BASIC_RIFT_PYLON, "Basic Rift Energy Collector");
        addBlock(RiftsBlocks.ADVANCED_RIFT_PYLON, "Advanced Rift Energy Collector");
        addBlock(RiftsBlocks.ELITE_RIFT_PYLON, "Elite Rift Energy Collector");
        addBlock(RiftsBlocks.ULTIMATE_RIFT_PYLON, "Ultimate Rift Energy Collector");

        addBlock(RiftsBlocks.RIFT_STONE, "Rift Stone");
        addBlock(RiftsBlocks.RIFT_STEEL_BLOCK, "Block of Rift Steel");
        addBlock(RiftsBlocks.RIFT_LEAVES, "Rift Leaves");
        addBlock(RiftsBlocks.RIFT_LOG, "Rift Log");
        addBlock(RiftsBlocks.RIFT_PLANKS, "Rift Planks");
        addBlock(RiftsBlocks.RIFT_PLANK_STAIRS, "Rift Plank Stairs");
        addBlock(RiftsBlocks.RIFT_PLANK_SLAB, "Rift Plank Slab");
        addBlock(RiftsBlocks.RIFT_SIGN, "Rift Sign");
        addBlock(RiftsBlocks.RIFT_WALL_SIGN, "Rift Sign");
        addBlock(RiftsBlocks.RIFT_HANGING_SIGN, "Rift Hanging Sign");
        addBlock(RiftsBlocks.RIFT_WALL_HANGING_SIGN, "Rift Hanging Sign");
        addBlock(RiftsBlocks.RIFT_WOOD, "Rift Wood");
        addBlock(RiftsBlocks.STRIPPED_RIFT_LOG, "Stripped Rift Log");
        addBlock(RiftsBlocks.STRIPPED_RIFT_WOOD, "Stripped Rift Wood");
        addBlock(RiftsBlocks.RIFT_FENCE, "Rift Fence");
        addBlock(RiftsBlocks.RIFT_FENCE_GATE, "Rift Fence Gate");
        addBlock(RiftsBlocks.RIFT_TRAPDOOR, "Rift Trapdoor");
        addBlock(RiftsBlocks.RIFT_PRESSURE_PLATE, "Rift Pressure Plate");
        addBlock(RiftsBlocks.RIFT_DOOR, "Rift Door");
        addBlock(RiftsBlocks.RIFT_BUTTON, "Rift Button");
        addBlock(RiftsBlocks.RIFT_STONE_STAIRS, "Rift Stone Stairs");
        addBlock(RiftsBlocks.RIFT_STONE_SLAB, "Rift Stone Slab");
        addBlock(RiftsBlocks.RIFT_STONE_WALL, "Rift Stone Wall");
        addBlock(RiftsBlocks.RIFT_STONE_BUTTON, "Rift Stone Button");
        addBlock(RiftsBlocks.RIFT_STONE_PRESSURE_PLATE, "Rift Stone Pressure Plate");
        addBlock(RiftsBlocks.POLISHED_RIFT_STONE, "Polished Rift Stone");
        addBlock(RiftsBlocks.POLISHED_RIFT_STONE_STAIRS, "Polished Rift Stone Stairs");
        addBlock(RiftsBlocks.POLISHED_RIFT_STONE_SLAB, "Polished Rift Stone Slab");
        addBlock(RiftsBlocks.POLISHED_RIFT_STONE_WALL, "Polished Rift Stone Wall");
        addBlock(RiftsBlocks.RIFT_STONE_BRICKS, "Rift Stone Bricks");
        addBlock(RiftsBlocks.RIFT_STONE_BRICK_STAIRS, "Rift Stone Brick Stairs");
        addBlock(RiftsBlocks.RIFT_STONE_BRICK_SLAB, "Rift Stone Brick Slab");
        addBlock(RiftsBlocks.RIFT_STONE_BRICK_WALL, "Rift Stone Brick Wall");
        addBlock(RiftsBlocks.CRACKED_RIFT_STONE_BRICKS, "Cracked Rift Stone Bricks");
        addBlock(RiftsBlocks.CHISELED_RIFT_STONE_BRICKS, "Chiseled Rift Stone Bricks");

        addItem(RiftsItems.DISPLACER, "Displacer");
        addItem(RiftsItems.RIFT_SCANNER, "Rift Scanner");
        addItem(RiftsItems.RIFT_PORTAL_GUN, "Rift Portal Gun");
        addItem(RiftsItems.RIFT_WRENCH, "Rift Wrench");
        addItem(RiftsItems.RIFT_PEARL, "Rift Pearl");
        addItem(RiftsItems.BASIC_RIFT_FOCUS, "Basic Rift Focus");
        addItem(RiftsItems.ADVANCED_RIFT_FOCUS, "Advanced Rift Focus");
        addItem(RiftsItems.ELITE_RIFT_FOCUS, "Elite Rift Focus");
        addItem(RiftsItems.ULTIMATE_RIFT_FOCUS, "Ultimate Rift Focus");
        addItem(RiftsItems.RIFT_STEEL_INGOT, "Rift Steel Ingot");
        addItem(RiftsItems.RIFT_STEEL_NUGGET, "Rift Steel Nugget");
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
        addEntityType(RiftsEntities.DISPLACER, "Displacer");
        addEntityType(RiftsEntities.RIFT_PORTAL, "Rift Portal");
        addEntityType(RiftsEntities.RIFT_ELEMENTAL, "Rift Elemental");
        addEntityType(RiftsEntities.NATURAL_RIFT, "Rift");
        add("message.rifts.rift_needs_armor", "The rift rejects you. You need a full set of rift steel armor");

        add("tooltip.rifts.rift_portal_gun", "Right click fires the first portal and left click fires the linked second portal, each using %1$s rift energy. Anyone can walk through them.");
        add("tooltip.rifts.portal_gun_colors", "Portals: %1$s and %2$s");
        add("message.rifts.portal_gun_empty", "The rift portal gun is out of rift energy");
        add("tooltip.rifts.stored_energy", "Stored Rift Energy: %1$s");
        add("tooltip.rifts.displacer_dimension", "Uses the conversions for the %1$s dimension.");
        add("tooltip.rifts.item_energy", "Rift Energy: %1$s / %2$s");
        add("gui.rifts.energy", "Rift Energy: %1$s / %2$s");

        add("jei.rifts.displacer_conversions", "Displacer Conversions");
        add("jei.rifts.crusher", "Crushing");
        add("jei.rifts.infuser", "Infusing");
        add("jei.rifts.converter", "Converting");
        add("jei.rifts.converter.energy", "%1$s RE/tick, %2$s ticks");
        add("jei.rifts.converter.from", "Home dimension: %1$s");
        add("jei.rifts.converter.to", "Linked to: %1$s");
        add("jei.rifts.quarry", "Quarrying");
        add("jei.rifts.quarry.energy", "%1$s RE/tick, %2$s ticks");
        add("jei.rifts.infuser.energy", "%1$s RE/tick");
        add("jei.rifts.chance", "Chance: %1$s%%");

        add("tooltip.rifts.basic_tick_accelerator", ACCELERATOR_TOOLTIP);
        add("tooltip.rifts.advanced_tick_accelerator", ACCELERATOR_TOOLTIP);
        add("tooltip.rifts.elite_tick_accelerator", ACCELERATOR_TOOLTIP);
        add("tooltip.rifts.ultimate_tick_accelerator", ACCELERATOR_TOOLTIP);

        add("tooltip.rifts.basic_rift_pylon", PYLON_TOOLTIP);
        add("tooltip.rifts.advanced_rift_pylon", PYLON_TOOLTIP);
        add("tooltip.rifts.elite_rift_pylon", PYLON_TOOLTIP);
        add("tooltip.rifts.ultimate_rift_pylon", PYLON_TOOLTIP);

        add("message.rifts.rift_scanner", "Rift Energy: %1$s / %2$s (%3$s%%)  |  Richness x%4$s");
        add("message.rifts.converter_select", "Converter selected. Click a converter in another dimension to link them");
        add("message.rifts.converter_linked", "Converters linked");
        add("message.rifts.converter_unlinked", "Converter unlinked");
        add("message.rifts.converter_cancelled", "Linking cancelled");
        add("message.rifts.converter_same_dimension", "Linked converters must be in different dimensions");
        add("message.rifts.converter_invalid", "That converter is not loaded or no longer exists");
        add("message.rifts.converter_not_linked", "Not linked");
        add("message.rifts.converter_linked_to", "Linked to %1$s");
        add("message.rifts.converter_status", "Rift Energy: %1$s / %2$s | %3$s");
        add("message.rifts.surge_incoming", "Rift Surge: wave %1$s of %2$s incoming");
        add("message.rifts.surge_wave", "Rift Surge: wave %1$s of %2$s");
        add("message.rifts.surge_cleared", "The rift surge has been cleared");
        add("message.rifts.surge_failed", "%1$s fell to the rift surge. The surge collapses");
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
