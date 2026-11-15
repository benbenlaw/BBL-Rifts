package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.rifts.block.capability.RiftEnergyHandler;
import com.benbenlaw.rifts.block.capability.RiftsCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

public class RiftCreativeStorageBlock extends Block {

    public RiftCreativeStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.invalidateCapabilities(pos);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        level.invalidateCapabilities(pos);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        for (Direction direction : Direction.values()) {
            RiftEnergyHandler handler = level.getCapability(RiftsCapabilities.RIFT_ENERGY, pos.relative(direction), direction.getOpposite());
            if (handler == null || !handler.canInsert()) continue;

            try (Transaction transaction = Transaction.openRoot()) {
                if (handler.insert(Integer.MAX_VALUE, transaction) > 0) {
                    transaction.commit();
                }
            }
        }
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 1);
        }
    }
}
