package com.benbenlaw.rifts.datamaps;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class RiftsDataMaps {

    public static final DataMapType<Item, DisplacerConversions> DISPLACER_HIT_RESULTS =
            DataMapType.builder(
                    Rifts.identifier("displacer_hit_results"),
                    Registries.ITEM,
                    DisplacerConversions.CODEC
            ).synced(DisplacerConversions.CODEC, true).build();

    public static final DataMapType<Biome, RiftEnergyModifier> BIOME_RIFT_ENERGY =
            DataMapType.builder(
                    Rifts.identifier("rift_energy"),
                    Registries.BIOME,
                    RiftEnergyModifier.CODEC
            ).build();
}
