package com.benbenlaw.rifts.block;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.entity.RiftChargerBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftCrusherBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftFurnaceBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftGeneratorBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftInfuserBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftPipeBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftPylonBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftStorageBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftTickAcceleratorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class RiftsBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Rifts.MOD_ID);

    public static final Supplier<BlockEntityType<RiftGeneratorBlockEntity>> RIFT_GENERATOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_generator_block_entity", () ->
                    new BlockEntityType<>(RiftGeneratorBlockEntity::new, RiftsBlocks.RIFT_GENERATOR.get()));

    public static final Supplier<BlockEntityType<RiftInfuserBlockEntity>> RIFT_INFUSER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_infuser_block_entity", () ->
                    new BlockEntityType<>(RiftInfuserBlockEntity::new, RiftsBlocks.RIFT_INFUSER.get()));

    public static final Supplier<BlockEntityType<RiftChargerBlockEntity>> RIFT_CHARGER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_charger_block_entity", () ->
                    new BlockEntityType<>(RiftChargerBlockEntity::new, RiftsBlocks.RIFT_CHARGER.get()));

    public static final Supplier<BlockEntityType<RiftCrusherBlockEntity>> RIFT_CRUSHER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_crusher_block_entity", () ->
                    new BlockEntityType<>(RiftCrusherBlockEntity::new, RiftsBlocks.RIFT_CRUSHER.get()));

    public static final Supplier<BlockEntityType<RiftFurnaceBlockEntity>> RIFT_FURNACE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_furnace_block_entity", () ->
                    new BlockEntityType<>(RiftFurnaceBlockEntity::new, RiftsBlocks.RIFT_FURNACE.get()));

    public static final Supplier<BlockEntityType<RiftPylonBlockEntity>> RIFT_PYLON_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_pylon_block_entity", () ->
                    new BlockEntityType<>(RiftPylonBlockEntity::new, Set.of(
                            RiftsBlocks.BASIC_RIFT_PYLON.get(),
                            RiftsBlocks.ADVANCED_RIFT_PYLON.get(),
                            RiftsBlocks.ELITE_RIFT_PYLON.get(),
                            RiftsBlocks.ULTIMATE_RIFT_PYLON.get())));

    public static final Supplier<BlockEntityType<RiftTickAcceleratorBlockEntity>> RIFT_TICK_ACCELERATOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_tick_accelerator_block_entity", () ->
                    new BlockEntityType<>(RiftTickAcceleratorBlockEntity::new, Set.of(
                            RiftsBlocks.BASIC_TICK_ACCELERATOR.get(),
                            RiftsBlocks.ADVANCED_TICK_ACCELERATOR.get(),
                            RiftsBlocks.ELITE_TICK_ACCELERATOR.get(),
                            RiftsBlocks.ULTIMATE_TICK_ACCELERATOR.get())));

    public static final Supplier<BlockEntityType<RiftStorageBlockEntity>> RIFT_STORAGE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_storage_block_entity", () ->
                    new BlockEntityType<>(RiftStorageBlockEntity::new, RiftsBlocks.RIFT_STORAGE.get()));

    public static final Supplier<BlockEntityType<RiftPipeBlockEntity>> RIFT_PIPE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_pipe_block_entity", () ->
                    new BlockEntityType<>(RiftPipeBlockEntity::new, RiftsBlocks.RIFT_PIPE.get()));

}
