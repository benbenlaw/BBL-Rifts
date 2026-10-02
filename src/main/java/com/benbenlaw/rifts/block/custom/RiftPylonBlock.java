package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.entity.RiftPylonBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RiftPylonBlock extends SyncableBlock {

    int capacity;
    int drawPerSecond;

    public static final MapCodec<RiftPylonBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->

            instance.group(
                        propertiesCodec(),
                            Codec.INT.fieldOf("capacity").forGetter(RiftPylonBlock::getCapacity),
                            Codec.INT.fieldOf("draw_per_second").forGetter(RiftPylonBlock::getDrawPerSecond)
                    ).apply(instance, RiftPylonBlock::new));

    public @NotNull MapCodec<RiftPylonBlock> codec() {
        return CODEC;
    }

    public RiftPylonBlock(Properties properties, int capacity, int drawPerSecond) {
        super(properties);
        this.capacity = capacity;
        this.drawPerSecond = drawPerSecond;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDrawPerSecond() {
        return drawPerSecond;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof RiftPylonBlockEntity pylon) {
                player.openMenu(new SimpleMenuProvider(pylon, pylon.getDisplayName()), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftPylonBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, RiftsBlockEntities.RIFT_PYLON_BLOCK_ENTITY.get(),
                (thisLevel, thisPos, thisState, thisEntity) -> thisEntity.tick());
    }
}
