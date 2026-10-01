package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RiftsLangProvider extends LanguageProvider {

    private static final String PYLON_TOOLTIP = "Stores %1$s rift energy and draws %2$s per second from the chunk it is in.";

    public RiftsLangProvider(PackOutput output) {
        super(output, Rifts.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + Rifts.MOD_ID, "Rifts");

        addBlock(RiftsBlocks.RIFT_GENERATOR, "Rift Generator");
        addBlock(RiftsBlocks.RIFT_INFUSER, "Rift Infuser");
        addBlock(RiftsBlocks.RIFT_STORAGE, "Rift Storage");
        addBlock(RiftsBlocks.RIFT_PIPE, "Rift Pipe");
        addBlock(RiftsBlocks.BASIC_RIFT_PYLON, "Basic Rift Pylon");
        addBlock(RiftsBlocks.ADVANCED_RIFT_PYLON, "Advanced Rift Pylon");
        addBlock(RiftsBlocks.ELITE_RIFT_PYLON, "Elite Rift Pylon");
        addBlock(RiftsBlocks.ULTIMATE_RIFT_PYLON, "Ultimate Rift Pylon");

        addItem(RiftsItems.DISPLACER, "Displacer");
        addItem(RiftsItems.RIFT_SCANNER, "Rift Scanner");
        addItem(RiftsItems.RIFT_WRENCH, "Rift Wrench");
        addItem(RiftsItems.RIFT_STEEL_INGOT, "Rift Steel Ingot");
        addItem(RiftsItems.RIFT_ELEMENTAL_SPAWN_EGG, "Rift Elemental Spawn Egg");
        addEntityType(EpochopolisEntities.DISPLACER, "Displacer");
        addEntityType(EpochopolisEntities.RIFT_ELEMENTAL, "Rift Elemental");

        add("tooltip.rifts.stored_energy", "Stored Rift Energy: %1$s");

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
