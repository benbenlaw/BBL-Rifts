package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class RiftsBlockLootProvider extends BlockLootSubProvider {

    public RiftsBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropWithRiftEnergy(RiftsBlocks.RIFT_GENERATOR.get());
        dropWithRiftEnergy(RiftsBlocks.RIFT_INFUSER.get());
        dropWithRiftEnergy(RiftsBlocks.RIFT_STORAGE.get());
        dropWithRiftEnergy(RiftsBlocks.BASIC_RIFT_PYLON.get());
        dropWithRiftEnergy(RiftsBlocks.ADVANCED_RIFT_PYLON.get());
        dropWithRiftEnergy(RiftsBlocks.ELITE_RIFT_PYLON.get());
        dropWithRiftEnergy(RiftsBlocks.ULTIMATE_RIFT_PYLON.get());

        dropSelf(RiftsBlocks.RIFT_PIPE.get());
        dropSelf(RiftsBlocks.RIFT_STEEL_BLOCK.get());
    }

    private void dropWithRiftEnergy(Block block) {
        add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                        .include(RiftsDataComponents.RIFT_ENERGY.get())))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return RiftsBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).map(block -> (Block) block).toList();
    }
}
