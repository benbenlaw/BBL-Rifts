package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.entity.RiftTickAcceleratorBlockEntity;
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

public class RiftTickAcceleratorBlock extends SyncableBlock {

    private final int extraTicks;
    private final int energyPerTick;

    public static final MapCodec<RiftTickAcceleratorBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.INT.fieldOf("extra_ticks").forGetter(RiftTickAcceleratorBlock::getExtraTicks),
                    Codec.INT.fieldOf("energy_per_tick").forGetter(RiftTickAcceleratorBlock::getEnergyPerTick)
            ).apply(instance, RiftTickAcceleratorBlock::new));

    public @NotNull MapCodec<RiftTickAcceleratorBlock> codec() {
        return CODEC;
    }

    public RiftTickAcceleratorBlock(Properties properties, int extraTicks, int energyPerTick) {
        super(properties);
        this.extraTicks = extraTicks;
        this.energyPerTick = energyPerTick;
    }

    public int getExtraTicks() {
        return extraTicks;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof RiftTickAcceleratorBlockEntity accelerator) {
                player.openMenu(new SimpleMenuProvider(accelerator, accelerator.getDisplayName()), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftTickAcceleratorBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, RiftsBlockEntities.RIFT_TICK_ACCELERATOR_BLOCK_ENTITY.get(),
                (thisLevel, thisPos, thisState, thisEntity) -> thisEntity.tick());
    }
}
