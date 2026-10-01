package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.rifts.block.entity.RiftPipeBlockEntity;
import com.benbenlaw.rifts.block.pipe.RiftPipeNetworks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class RiftPipeBlock extends BaseEntityBlock {

    public static final MapCodec<RiftPipeBlock> CODEC = simpleCodec(RiftPipeBlock::new);

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = new EnumMap<>(Map.of(
            Direction.NORTH, BlockStateProperties.NORTH,
            Direction.EAST, BlockStateProperties.EAST,
            Direction.SOUTH, BlockStateProperties.SOUTH,
            Direction.WEST, BlockStateProperties.WEST,
            Direction.UP, BlockStateProperties.UP,
            Direction.DOWN, BlockStateProperties.DOWN
    ));

    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Map.of(
            Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5),
            Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16),
            Direction.WEST, Block.box(0, 5, 5, 5, 11, 11),
            Direction.EAST, Block.box(11, 5, 5, 16, 11, 11),
            Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11),
            Direction.UP, Block.box(5, 11, 5, 11, 16, 11)
    ));

    public RiftPipeBlock(Properties properties) {
        super(properties);
        BlockState state = this.defaultBlockState();
        for (BooleanProperty property : PROPERTY_BY_DIRECTION.values()) {
            state = state.setValue(property, false);
        }
        this.registerDefaultState(state);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        for (BooleanProperty property : PROPERTY_BY_DIRECTION.values()) {
            builder.add(property);
        }
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        VoxelShape shape = CORE;
        for (Direction direction : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
                shape = Shapes.or(shape, ARMS.get(direction));
            }
        }
        return shape;
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        RiftPipeNetworks.markDirty(level);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftPipeBlockEntity(pos, state);
    }
}
