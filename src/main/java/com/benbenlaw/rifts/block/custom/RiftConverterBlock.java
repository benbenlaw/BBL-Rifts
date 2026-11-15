package com.benbenlaw.rifts.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.entity.RiftConverterBlockEntity;
import com.benbenlaw.rifts.entity.RiftsAttachments;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RiftConverterBlock extends SyncableBlock {

    public static final MapCodec<RiftConverterBlock> CODEC = simpleCodec(RiftConverterBlock::new);

    public @NotNull MapCodec<RiftConverterBlock> codec() {
        return CODEC;
    }

    public RiftConverterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!stack.is(Tags.Items.TOOLS_WRENCH)) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof RiftConverterBlockEntity here)) return InteractionResult.SUCCESS;

        GlobalPos me = GlobalPos.of(level.dimension(), pos);

        if (!player.hasData(RiftsAttachments.PENDING_CONVERTER_LINK)) {
            if (here.getLink() != null) {
                here.unlink(serverLevel);
                player.sendOverlayMessage(Component.translatable("message.rifts.converter_unlinked"));
            } else {
                player.setData(RiftsAttachments.PENDING_CONVERTER_LINK, me);
                player.sendOverlayMessage(Component.translatable("message.rifts.converter_select"));
            }
            return InteractionResult.SUCCESS;
        }

        GlobalPos pending = player.getData(RiftsAttachments.PENDING_CONVERTER_LINK);
        player.removeData(RiftsAttachments.PENDING_CONVERTER_LINK);

        if (pending.equals(me)) {
            player.sendOverlayMessage(Component.translatable("message.rifts.converter_cancelled"));
            return InteractionResult.SUCCESS;
        }
        if (pending.dimension().equals(level.dimension())) {
            player.sendOverlayMessage(Component.translatable("message.rifts.converter_same_dimension"));
            return InteractionResult.SUCCESS;
        }

        ServerLevel otherLevel = serverLevel.getServer().getLevel(pending.dimension());
        if (otherLevel == null || !otherLevel.hasChunkAt(pending.pos())
                || !(otherLevel.getBlockEntity(pending.pos()) instanceof RiftConverterBlockEntity other)) {
            player.sendOverlayMessage(Component.translatable("message.rifts.converter_invalid"));
            return InteractionResult.SUCCESS;
        }

        here.unlink(serverLevel);
        other.unlink(otherLevel);
        here.setLink(pending);
        other.setLink(me);
        player.sendOverlayMessage(Component.translatable("message.rifts.converter_linked"));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RiftConverterBlockEntity converter) {
            GlobalPos link = converter.getLink();
            Component linkText = link == null
                    ? Component.translatable("message.rifts.converter_not_linked")
                    : Component.translatable("message.rifts.converter_linked_to", link.dimension().identifier().toString());
            player.sendOverlayMessage(Component.translatable("message.rifts.converter_status",
                    converter.getRiftEnergyHandler().getAmountAsInt(), converter.getRiftEnergyHandler().getCapacityAsInt(), linkText));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftConverterBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, RiftsBlockEntities.RIFT_CONVERTER_BLOCK_ENTITY.get(),
                (thisLevel, thisPos, thisState, thisEntity) -> thisEntity.tick());
    }
}
