package com.benbenlaw.rifts.recipe.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateResult(BlockState state) {

    public static final MapCodec<BlockStateResult> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockState.CODEC.fieldOf("block_state").forGetter(BlockStateResult::state)
    ).apply(inst, BlockStateResult::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateResult> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), BlockStateResult::state,
            BlockStateResult::new
    );
}
