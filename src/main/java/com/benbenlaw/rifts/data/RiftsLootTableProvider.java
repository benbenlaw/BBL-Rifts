package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class RiftsLootTableProvider extends LootTableProvider {

    public RiftsLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(RiftsBlockLootProvider::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class EntityLoot extends EntityLootSubProvider {

        protected EntityLoot(HolderLookup.Provider registries) {
            super(FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        public void generate() {
            add(EpochopolisEntities.RIFT_ELEMENTAL.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .add(LootItem.lootTableItem(RiftsItems.RIFT_STEEL_INGOT.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(9, 14)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(2, 4))))));
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return Stream.of(EpochopolisEntities.RIFT_ELEMENTAL.get());
        }
    }
}
