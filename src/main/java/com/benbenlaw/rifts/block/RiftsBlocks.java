package com.benbenlaw.rifts.block;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.custom.RiftChargerBlock;
import com.benbenlaw.rifts.block.custom.RiftConverterBlock;
import com.benbenlaw.rifts.block.custom.RiftCreativeStorageBlock;
import com.benbenlaw.rifts.block.custom.RiftCrusherBlock;
import com.benbenlaw.rifts.block.custom.RiftFurnaceBlock;
import com.benbenlaw.rifts.block.custom.RiftGeneratorBlock;
import com.benbenlaw.rifts.block.custom.RiftInfuserBlock;
import com.benbenlaw.rifts.block.custom.RiftPipeBlock;
import com.benbenlaw.rifts.block.custom.RiftQuarryBlock;
import com.benbenlaw.rifts.block.custom.RiftSurgeCoreBlock;
import com.benbenlaw.rifts.block.custom.RiftPylonBlock;
import com.benbenlaw.rifts.block.custom.RiftStorageBlock;
import com.benbenlaw.rifts.block.custom.RiftTickAcceleratorBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsItems;
import com.benbenlaw.rifts.screen.MachineLayouts;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
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

    public static final DeferredBlock<Block> BASIC_RIFT_CRUSHER = registerBlock("basic_rift_crusher",
            properties -> new RiftCrusherBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[0]));

    public static final DeferredBlock<Block> ADVANCED_RIFT_CRUSHER = registerBlock("advanced_rift_crusher",
            properties -> new RiftCrusherBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[1]));

    public static final DeferredBlock<Block> ELITE_RIFT_CRUSHER = registerBlock("elite_rift_crusher",
            properties -> new RiftCrusherBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[2]));

    public static final DeferredBlock<Block> ULTIMATE_RIFT_CRUSHER = registerBlock("ultimate_rift_crusher",
            properties -> new RiftCrusherBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[3]));

    public static final List<DeferredBlock<Block>> RIFT_CRUSHERS = List.of(BASIC_RIFT_CRUSHER, ADVANCED_RIFT_CRUSHER, ELITE_RIFT_CRUSHER, ULTIMATE_RIFT_CRUSHER);

    public static final DeferredBlock<Block> RIFT_CONVERTER = registerBlock("rift_converter",
            properties -> new RiftConverterBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_SURGE_CORE = registerBlock("rift_surge_core",
            properties -> new RiftSurgeCoreBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_QUARRY = registerBlock("rift_quarry",
            properties -> new RiftQuarryBlock(machineProperties(properties).lightLevel(litBlockEmission())));

    public static final DeferredBlock<Block> BASIC_RIFT_FURNACE = registerBlock("basic_rift_furnace",
            properties -> new RiftFurnaceBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[0]));

    public static final DeferredBlock<Block> ADVANCED_RIFT_FURNACE = registerBlock("advanced_rift_furnace",
            properties -> new RiftFurnaceBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[1]));

    public static final DeferredBlock<Block> ELITE_RIFT_FURNACE = registerBlock("elite_rift_furnace",
            properties -> new RiftFurnaceBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[2]));

    public static final DeferredBlock<Block> ULTIMATE_RIFT_FURNACE = registerBlock("ultimate_rift_furnace",
            properties -> new RiftFurnaceBlock(machineProperties(properties).lightLevel(litBlockEmission()), MachineLayouts.TIER_LINES[3]));

    public static final List<DeferredBlock<Block>> RIFT_FURNACES = List.of(BASIC_RIFT_FURNACE, ADVANCED_RIFT_FURNACE, ELITE_RIFT_FURNACE, ULTIMATE_RIFT_FURNACE);

    public static final DeferredBlock<Block> CREATIVE_RIFT_STORAGE = registerBlock("creative_rift_storage",
            properties -> new RiftCreativeStorageBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_STORAGE = registerBlock("rift_storage",
            properties -> new RiftStorageBlock(machineProperties(properties)));

    public static final DeferredBlock<Block> RIFT_PIPE = registerBlock("rift_pipe",
            properties -> new RiftPipeBlock(properties.strength(1.5f).noOcclusion()));

    public static final DeferredBlock<Block> RIFT_STEEL_BLOCK = registerBlock("rift_steel_block",
            properties -> new Block(properties.sound(SoundType.METAL).strength(5.0f)));

    public static final DeferredBlock<Block> RIFT_STONE =registerBlock("rift_stone",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone")))));

    public static final DeferredBlock<Block> RIFT_LEAVES = registerBlock("rift_leaves",
            properties -> new UntintedParticleLeavesBlock(0.01F, ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, 0xFF8A4DDB),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_leaves")))));

    public static final DeferredBlock<Block> RIFT_LOG = registerBlock("rift_log",
            properties -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_log")))));

    public static final DeferredBlock<Block> RIFT_PLANKS = registerBlock("rift_planks",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_planks")))));

    public static final DeferredBlock<Block> RIFT_PLANK_STAIRS = registerBlock("rift_plank_stairs",
            properties -> new StairBlock(RIFT_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_plank_stairs")))));

    public static final DeferredBlock<Block> RIFT_PLANK_SLAB = registerBlock("rift_plank_slab",
            properties -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_plank_slab")))));

    public static final DeferredBlock<Block> RIFT_WOOD = registerBlock("rift_wood",
            properties -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_wood")))));

    public static final DeferredBlock<Block> STRIPPED_RIFT_LOG = registerBlock("stripped_rift_log",
            properties -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("stripped_rift_log")))));

    public static final DeferredBlock<Block> STRIPPED_RIFT_WOOD = registerBlock("stripped_rift_wood",
            properties -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("stripped_rift_wood")))));

    public static final DeferredBlock<Block> RIFT_FENCE = registerBlock("rift_fence",
            properties -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_fence")))));

    public static final DeferredBlock<Block> RIFT_FENCE_GATE = registerBlock("rift_fence_gate",
            properties -> new FenceGateBlock(RiftsWoodTypes.RIFT, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_fence_gate")))));

    public static final DeferredBlock<Block> RIFT_TRAPDOOR = registerBlock("rift_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_trapdoor")))));

    public static final DeferredBlock<Block> RIFT_PRESSURE_PLATE = registerBlock("rift_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_pressure_plate")))));

    public static final DeferredBlock<Block> RIFT_DOOR = registerBlock("rift_door",
            properties -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_door")))));

    public static final DeferredBlock<Block> RIFT_BUTTON = registerBlock("rift_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_button")))));

    public static final DeferredBlock<Block> RIFT_STONE_STAIRS = registerBlock("rift_stone_stairs",
            properties -> new StairBlock(RIFT_STONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_STAIRS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_stairs")))));

    public static final DeferredBlock<Block> RIFT_STONE_SLAB = registerBlock("rift_stone_slab",
            properties -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_SLAB).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_slab")))));

    public static final DeferredBlock<Block> RIFT_STONE_WALL = registerBlock("rift_stone_wall",
            properties -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_WALL).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_wall")))));

    public static final DeferredBlock<Block> RIFT_STONE_BUTTON = registerBlock("rift_stone_button",
            properties -> new ButtonBlock(BlockSetType.STONE, 20, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BUTTON).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_button")))));

    public static final DeferredBlock<Block> RIFT_STONE_PRESSURE_PLATE = registerBlock("rift_stone_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_pressure_plate")))));

    public static final DeferredBlock<Block> POLISHED_RIFT_STONE = registerBlock("polished_rift_stone",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("polished_rift_stone")))));

    public static final DeferredBlock<Block> POLISHED_RIFT_STONE_STAIRS = registerBlock("polished_rift_stone_stairs",
            properties -> new StairBlock(POLISHED_RIFT_STONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_STAIRS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("polished_rift_stone_stairs")))));

    public static final DeferredBlock<Block> POLISHED_RIFT_STONE_SLAB = registerBlock("polished_rift_stone_slab",
            properties -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_SLAB).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("polished_rift_stone_slab")))));

    public static final DeferredBlock<Block> POLISHED_RIFT_STONE_WALL = registerBlock("polished_rift_stone_wall",
            properties -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_WALL).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("polished_rift_stone_wall")))));

    public static final DeferredBlock<Block> RIFT_STONE_BRICKS = registerBlock("rift_stone_bricks",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_bricks")))));

    public static final DeferredBlock<Block> RIFT_STONE_BRICK_STAIRS = registerBlock("rift_stone_brick_stairs",
            properties -> new StairBlock(RIFT_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_STAIRS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_brick_stairs")))));

    public static final DeferredBlock<Block> RIFT_STONE_BRICK_SLAB = registerBlock("rift_stone_brick_slab",
            properties -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_SLAB).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_brick_slab")))));

    public static final DeferredBlock<Block> RIFT_STONE_BRICK_WALL = registerBlock("rift_stone_brick_wall",
            properties -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICK_WALL).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_stone_brick_wall")))));

    public static final DeferredBlock<Block> CRACKED_RIFT_STONE_BRICKS = registerBlock("cracked_rift_stone_bricks",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("cracked_rift_stone_bricks")))));

    public static final DeferredBlock<Block> CHISELED_RIFT_STONE_BRICKS = registerBlock("chiseled_rift_stone_bricks",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("chiseled_rift_stone_bricks")))));

    public static final DeferredBlock<Block> RIFT_SIGN = BLOCKS.registerBlock("rift_sign",
            properties -> new StandingSignBlock(RiftsWoodTypes.RIFT, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_sign")))));

    public static final DeferredBlock<Block> RIFT_WALL_SIGN = BLOCKS.registerBlock("rift_wall_sign",
            properties -> new WallSignBlock(RiftsWoodTypes.RIFT, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_wall_sign"))).overrideLootTable(RIFT_SIGN.get().getLootTable())));

    public static final DeferredBlock<Block> RIFT_HANGING_SIGN = BLOCKS.registerBlock("rift_hanging_sign",
            properties -> new CeilingHangingSignBlock(RiftsWoodTypes.RIFT, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_hanging_sign")))));

    public static final DeferredBlock<Block> RIFT_WALL_HANGING_SIGN = BLOCKS.registerBlock("rift_wall_hanging_sign",
            properties -> new WallHangingSignBlock(RiftsWoodTypes.RIFT, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).setId(ResourceKey.create(Registries.BLOCK, Rifts.identifier("rift_wall_hanging_sign"))).overrideLootTable(RIFT_HANGING_SIGN.get().getLootTable())));

    public static final DeferredItem<Item> RIFT_SIGN_ITEM = RiftsItems.ITEMS.registerItem("rift_sign",
            properties -> new SignItem(RIFT_SIGN.get(), RIFT_WALL_SIGN.get(), properties.stacksTo(16).useBlockDescriptionPrefix()));

    public static final DeferredItem<Item> RIFT_HANGING_SIGN_ITEM = RiftsItems.ITEMS.registerItem("rift_hanging_sign",
            properties -> new HangingSignItem(RIFT_HANGING_SIGN.get(), RIFT_WALL_HANGING_SIGN.get(), properties.stacksTo(16).useBlockDescriptionPrefix()));

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

    public static void registerFlammability() {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        for (DeferredBlock<Block> block : List.of(RIFT_PLANKS, RIFT_PLANK_STAIRS, RIFT_PLANK_SLAB, RIFT_FENCE, RIFT_FENCE_GATE)) {
            fire.setFlammable(block.get(), 5, 20);
        }
        for (DeferredBlock<Block> block : List.of(RIFT_LOG, RIFT_WOOD, STRIPPED_RIFT_LOG, STRIPPED_RIFT_WOOD)) {
            fire.setFlammable(block.get(), 5, 5);
        }
        fire.setFlammable(RIFT_LEAVES.get(), 30, 60);
    }
}
