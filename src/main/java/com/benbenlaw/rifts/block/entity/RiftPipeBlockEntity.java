package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.pipe.PipeMode;
import com.benbenlaw.rifts.block.pipe.RiftPipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class RiftPipeBlockEntity extends BlockEntity {

    private final EnumMap<Direction, PipeMode> modes = new EnumMap<>(Direction.class);

    public RiftPipeBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_PIPE_BLOCK_ENTITY.get(), pos, state);
    }

    public @Nullable PipeMode getMode(Direction direction) {
        return modes.get(direction);
    }

    public void setMode(Direction direction, PipeMode mode) {
        modes.put(direction, mode);
        setChanged();
    }

    public void clearMode(Direction direction) {
        if (modes.remove(direction) != null) {
            setChanged();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            RiftPipeNetworks.register(serverLevel, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level instanceof ServerLevel serverLevel) {
            RiftPipeNetworks.unregister(serverLevel, worldPosition);
        }
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        for (Direction direction : Direction.values()) {
            PipeMode mode = modes.get(direction);
            if (mode != null) {
                output.putInt("mode_" + direction.getName(), mode.ordinal());
            }
        }
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        modes.clear();
        for (Direction direction : Direction.values()) {
            int ordinal = input.getIntOr("mode_" + direction.getName(), -1);
            if (ordinal >= 0 && ordinal < PipeMode.values().length) {
                modes.put(direction, PipeMode.values()[ordinal]);
            }
        }
        super.loadAdditional(input);
    }
}
