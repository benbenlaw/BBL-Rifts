package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.entity.RiftsEntities;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
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
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class RiftsEntityLootTableProvider extends LootTableProvider {

    public RiftsEntityLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(SurgeLoot::new, LootContextParamSets.CHEST),
                new SubProviderEntry(RiftsBlockLootProvider::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class SurgeLoot implements LootTableSubProvider {

        protected SurgeLoot(HolderLookup.Provider registries) {
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            output.accept(RiftsLootTables.SURGE_REWARD, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(RiftsItems.RIFT_STEEL_INGOT.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 16)))))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(RiftsItems.RIFT_PEARL.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .when(LootItemRandomChanceCondition.randomChance(0.4F))
                            .add(LootItem.lootTableItem(Items.DIAMOND)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .when(LootItemRandomChanceCondition.randomChance(0.15F))
                            .add(LootItem.lootTableItem(RiftsItems.ADVANCED_RIFT_FOCUS.get()))));
        }
    }

    private static class EntityLoot extends EntityLootSubProvider {

        protected EntityLoot(HolderLookup.Provider registries) {
            super(FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        public void generate() {

            add(RiftsEntities.RIFT_ELEMENTAL.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .add(LootItem.lootTableItem(RiftsItems.RIFT_STEEL_INGOT.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(9, 14)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(2, 4))))));
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return Stream.of(RiftsEntities.RIFT_ELEMENTAL.get());
        }
    }
}
