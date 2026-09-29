package com.benbenlaw.rifts.block;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.custom.RiftGeneratorBlock;
import com.benbenlaw.rifts.block.custom.RiftInfuserBlock;
import com.benbenlaw.rifts.block.custom.RiftPylonBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.EpochopolisItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
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

    public static final DeferredBlock<Block> SIMPLE_RIFT_PYLON = registerBlock("simple_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), RiftsStartupConfig.SIMPLE_PYLON_GENERATION_RATE.get()));

    public static final DeferredBlock<Block> ADVANCED_RIFT_PYLON = registerBlock("advanced_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), RiftsStartupConfig.ADVANCED_PYLON_GENERATION_RATE.get()));

    public static final DeferredBlock<Block> ELITE_RIFT_PYLON = registerBlock("elite_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), RiftsStartupConfig.ELITE_PYLON_GENERATION_RATE.get()));

    public static final DeferredBlock<Block> ULTIMATE_RIFT_PYLON = registerBlock("ultimate_rift_pylon",
            properties -> new RiftPylonBlock(machineProperties(properties), RiftsStartupConfig.ULTIMATE_PYLON_GENERATION_RATE.get()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        EpochopolisItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
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
