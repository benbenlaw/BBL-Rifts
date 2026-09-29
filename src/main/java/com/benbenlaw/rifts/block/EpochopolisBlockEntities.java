package com.benbenlaw.rifts.block;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.entity.RiftGeneratorBlockEntity;
import com.benbenlaw.rifts.block.entity.RiftInfuserBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EpochopolisBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Rifts.MOD_ID);

    public static final Supplier<BlockEntityType<RiftGeneratorBlockEntity>> RIFT_GENERATOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_generator_block_entity", () ->
                    new BlockEntityType<>(RiftGeneratorBlockEntity::new, RiftsBlocks.RIFT_GENERATOR.get()));

    public static final Supplier<BlockEntityType<RiftInfuserBlockEntity>> RIFT_INFUSER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift_infuser_block_entity", () ->
                    new BlockEntityType<>(RiftInfuserBlockEntity::new, RiftsBlocks.RIFT_INFUSER.get()));


}
