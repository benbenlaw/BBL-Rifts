package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.entity.RiftCrusherBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RiftCrusherBlock extends SyncableBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final MapCodec<RiftCrusherBlock> CODEC = simpleCodec(RiftCrusherBlock::new);

    public @NotNull MapCodec<RiftCrusherBlock> codec() {
        return CODEC;
    }

    public RiftCrusherBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof RiftCrusherBlockEntity crusher) {
                player.openMenu(new SimpleMenuProvider(crusher, crusher.getDisplayName()), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftCrusherBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, RiftsBlockEntities.RIFT_CRUSHER_BLOCK_ENTITY.get(),
                (thisLevel, thisPos, thisState, thisEntity) -> thisEntity.tick());
    }
}
