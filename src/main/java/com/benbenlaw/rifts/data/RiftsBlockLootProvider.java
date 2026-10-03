package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.Tags;
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
        dropWithRiftEnergy(RiftsBlocks.RIFT_CHARGER.get());
        dropWithRiftEnergy(RiftsBlocks.RIFT_CRUSHER.get());
        dropWithRiftEnergy(RiftsBlocks.RIFT_FURNACE.get());
        dropWithRiftEnergy(RiftsBlocks.BASIC_TICK_ACCELERATOR.get());
        dropWithRiftEnergy(RiftsBlocks.ADVANCED_TICK_ACCELERATOR.get());
        dropWithRiftEnergy(RiftsBlocks.ELITE_TICK_ACCELERATOR.get());
        dropWithRiftEnergy(RiftsBlocks.ULTIMATE_TICK_ACCELERATOR.get());
        dropWithRiftEnergy(RiftsBlocks.RIFT_STORAGE.get());
        dropWithRiftEnergy(RiftsBlocks.BASIC_RIFT_PYLON.get());
        dropWithRiftEnergy(RiftsBlocks.ADVANCED_RIFT_PYLON.get());
        dropWithRiftEnergy(RiftsBlocks.ELITE_RIFT_PYLON.get());
        dropWithRiftEnergy(RiftsBlocks.ULTIMATE_RIFT_PYLON.get());

        dropSelf(RiftsBlocks.CREATIVE_RIFT_STORAGE.get());
        dropSelf(RiftsBlocks.RIFT_PIPE.get());
        dropSelf(RiftsBlocks.RIFT_STEEL_BLOCK.get());
        dropSelf(RiftsBlocks.RIFT_STONE.get());
        dropSelf(RiftsBlocks.RIFT_LOG.get());
        // No sapling yet, so leaves give only the occasional stick unless sheared or silk touched
        add(RiftsBlocks.RIFT_LEAVES.get(), createSilkTouchOrShearsTagDispatchTable(RiftsBlocks.RIFT_LEAVES.get(),
                LootItem.lootTableItem(Items.STICK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .when(LootItemRandomChanceCondition.randomChance(0.1F))));
        dropSelf(RiftsBlocks.RIFT_PLANKS.get());
        dropSelf(RiftsBlocks.RIFT_PLANK_STAIRS.get());

        this.add(RiftsBlocks.RIFT_PLANK_SLAB.get(), this::createSlabItemTable);

    }

    // Vanilla only accepts the plain shears item, so match anything in the common shears tag instead
    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(registries.lookupOrThrow(Registries.ITEM), Tags.Items.TOOLS_SHEAR))
                .or(hasSilkTouch());
    }

    private LootTable.Builder createSilkTouchOrShearsTagDispatchTable(Block original, LootPoolEntryContainer.Builder<?> entry) {
        return createSelfDropDispatchTable(original, hasShearsOrSilkTouch(), entry);
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
