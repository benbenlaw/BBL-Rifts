package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.rifts.block.entity.RiftStorageBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RiftStorageBlock extends SyncableBlock {

    public static final MapCodec<RiftStorageBlock> CODEC = simpleCodec(RiftStorageBlock::new);

    public @NotNull MapCodec<RiftStorageBlock> codec() {
        return CODEC;
    }

    public RiftStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof RiftStorageBlockEntity storage) {
                player.openMenu(new SimpleMenuProvider(storage, storage.getDisplayName()), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftStorageBlockEntity(pos, state);
    }
}
