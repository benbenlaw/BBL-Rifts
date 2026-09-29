package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.rifts.block.EpochopolisBlockEntities;
import com.benbenlaw.rifts.block.entity.RiftGeneratorBlockEntity;
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

    int generatedAmount;

    public static final MapCodec<RiftPylonBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->

            instance.group(
                        propertiesCodec(),
                            Codec.INT.fieldOf("generated_amount").forGetter(RiftPylonBlock::getGeneratedAmount)
                    ).apply(instance, RiftPylonBlock::new));

    public @NotNull MapCodec<RiftPylonBlock> codec() {
        return CODEC;
    }

    public RiftPylonBlock(Properties properties, int generatedAmount) {
        super(properties);
        this.generatedAmount = generatedAmount;
    }

    public int getGeneratedAmount() {
        return generatedAmount;
    }
}
