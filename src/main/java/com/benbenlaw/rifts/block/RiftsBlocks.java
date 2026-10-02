package com.benbenlaw.rifts.block;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.custom.RiftChargerBlock;
import com.benbenlaw.rifts.block.custom.RiftCreativeStorageBlock;
import com.benbenlaw.rifts.block.custom.RiftCrusherBlock;
import com.benbenlaw.rifts.block.custom.RiftFurnaceBlock;
import com.benbenlaw.rifts.block.custom.RiftGeneratorBlock;
import com.benbenlaw.rifts.block.custom.RiftInfuserBlock;
import com.benbenlaw.rifts.block.custom.RiftPipeBlock;
import com.benbenlaw.rifts.block.custom.RiftPylonBlock;
import com.benbenlaw.rifts.block.custom.RiftStorageBlock;
import com.benbenlaw.rifts.block.custom.RiftTickAcceleratorBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class RiftsBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Rifts.MOD_ID);

    public static final DeferredBlock<Block> RIFT_GENERATOR = registerBlock("rift_generator",
            properties -> new RiftGeneratorBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_INFUSER = registerBlock("rift_infuser",
            properties -> new RiftInfuserBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> BASIC_RIFT_PYLON = registerBlock("basic_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), pylonCapacity(0), pylonDrawPerSecond(0)));

    public static final DeferredBlock<Block> ADVANCED_RIFT_PYLON = registerBlock("advanced_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), pylonCapacity(1), pylonDrawPerSecond(1)));

    public static final DeferredBlock<Block> ELITE_RIFT_PYLON = registerBlock("elite_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), pylonCapacity(2), pylonDrawPerSecond(2)));

    public static final DeferredBlock<Block> ULTIMATE_RIFT_PYLON = registerBlock("ultimate_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), pylonCapacity(3), pylonDrawPerSecond(3)));

    public static final DeferredBlock<Block> BASIC_TICK_ACCELERATOR = registerBlock("basic_tick_accelerator",
            properties -> new RiftTickAcceleratorBlock(machineProperties(properties), acceleratorExtraTicks(0), acceleratorEnergyPerTick(0)));

    public static final DeferredBlock<Block> ADVANCED_TICK_ACCELERATOR = registerBlock("advanced_tick_accelerator",
            properties -> new RiftTickAcceleratorBlock(machineProperties(properties), acceleratorExtraTicks(1), acceleratorEnergyPerTick(1)));

    public static final DeferredBlock<Block> ELITE_TICK_ACCELERATOR = registerBlock("elite_tick_accelerator",
            properties -> new RiftTickAcceleratorBlock(machineProperties(properties), acceleratorExtraTicks(2), acceleratorEnergyPerTick(2)));

    public static final DeferredBlock<Block> ULTIMATE_TICK_ACCELERATOR = registerBlock("ultimate_tick_accelerator",
            properties -> new RiftTickAcceleratorBlock(machineProperties(properties), acceleratorExtraTicks(3), acceleratorEnergyPerTick(3)));

    public static final DeferredBlock<Block> RIFT_CHARGER = registerBlock("rift_charger",
            properties -> new RiftChargerBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_CRUSHER = registerBlock("rift_crusher",
            properties -> new RiftCrusherBlock(machineProperties(properties).lightLevel(litBlockEmission())));

    public static final DeferredBlock<Block> RIFT_FURNACE = registerBlock("rift_furnace",
            properties -> new RiftFurnaceBlock(machineProperties(properties).lightLevel(litBlockEmission())));

    public static final DeferredBlock<Block> CREATIVE_RIFT_STORAGE = registerBlock("creative_rift_storage",
            properties -> new RiftCreativeStorageBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_STORAGE = registerBlock("rift_storage",
            properties -> new RiftStorageBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_PIPE = registerBlock("rift_pipe",
            properties -> new RiftPipeBlock(properties.strength(1.5f).noOcclusion()));

    public static final DeferredBlock<Block> RIFT_STEEL_BLOCK = registerBlock("rift_steel_block",
            properties -> new Block(properties.sound(SoundType.METAL).strength(5.0f)));

    public static final DeferredBlock<Block> RIFT_LOG = registerBlock("rift_log",
            properties -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_log")))));

    public static final DeferredBlock<Block> RIFT_PLANKS = registerBlock("rift_planks",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_planks")))));

    public static final DeferredBlock<Block> RIFT_PLANK_STAIRS = registerBlock("rift_plank_stairs",
            properties -> new StairBlock(RIFT_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_plank_stairs")))));

    public static final DeferredBlock<Block> RIFT_PLANK_SLAB = registerBlock("rift_plank_slab",
            properties -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_plank_slab")))));

    private static int acceleratorExtraTicks(int tier) {
        long extra = (long) RiftsStartupConfig.ACCELERATOR_BASE_EXTRA_TICKS.get() * (long) Math.pow(RiftsStartupConfig.ACCELERATOR_EXTRA_TICKS_MULTIPLIER.get(), tier);
        return (int) Math.min(Integer.MAX_VALUE, extra);
    }

    private static int acceleratorEnergyPerTick(int tier) {
        long energy = (long) RiftsStartupConfig.ACCELERATOR_BASE_ENERGY_PER_TICK.get() * (long) Math.pow(RiftsStartupConfig.ACCELERATOR_ENERGY_MULTIPLIER.get(), tier);
        return (int) Math.min(Integer.MAX_VALUE, energy);
    }

    private static int pylonCapacity(int tier) {
        long capacity = (long) RiftsStartupConfig.PYLON_BASE_CAPACITY.get() * (long) Math.pow(RiftsStartupConfig.PYLON_CAPACITY_MULTIPLIER.get(), tier);
        return (int) Math.min(Integer.MAX_VALUE, capacity);
    }

    private static int pylonDrawPerSecond(int tier) {
        long draw = (long) RiftsStartupConfig.PYLON_BASE_DRAW_PER_SECOND.get() * (long) Math.pow(RiftsStartupConfig.PYLON_DRAW_MULTIPLIER.get(), tier);
        return (int) Math.min(Integer.MAX_VALUE, draw);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        RiftsItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    private static ToIntFunction<BlockState> litBlockEmission() {
        return (lightLevel) -> lightLevel.getValue(BlockStateProperties.LIT) ? 9 : 0;
    }

    private static BlockBehaviour.Properties machineProperties(BlockBehaviour.Properties machineProperties) {
        return machineProperties
                .requiresCorrectToolForDrops()
                .strength(3.5f)
                .noOcclusion();
    }
}
