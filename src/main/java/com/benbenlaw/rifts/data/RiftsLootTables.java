package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class RiftsLootTables {

    public static final ResourceKey<LootTable> SURGE_REWARD = ResourceKey.create(Registries.LOOT_TABLE, Rifts.identifier("surge_reward"));
}
