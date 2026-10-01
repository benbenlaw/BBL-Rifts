package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.block.custom.RiftPipeBlock;
import com.benbenlaw.rifts.block.entity.RiftPipeBlockEntity;
import com.benbenlaw.rifts.block.pipe.PipeMode;
import com.benbenlaw.rifts.block.pipe.RiftPipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class RiftWrenchItem extends Item {

    public RiftWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockState(pos).getBlock() instanceof RiftPipeBlock)) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof RiftPipeBlockEntity pipe) {
            Direction side = sideFromHit(context.getClickLocation(), pos, context.getClickedFace());
            PipeMode mode = pipe.getMode(side);
            Player player = context.getPlayer();

            if (mode == null) {
                if (player != null) player.sendOverlayMessage(Component.translatable("message.rifts.pipe_no_connection"));
                return InteractionResult.SUCCESS;
            }

            PipeMode next = mode.next();
            pipe.setMode(side, next);
            RiftPipeNetworks.markDirty(level);
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("message.rifts.pipe_side",
                        Component.translatable("direction.rifts." + side.getName()),
                        Component.translatable(next.getTranslationKey())));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static Direction sideFromHit(Vec3 hit, BlockPos pos, Direction clickedFace) {
        double dx = hit.x - (pos.getX() + 0.5);
        double dy = hit.y - (pos.getY() + 0.5);
        double dz = hit.z - (pos.getZ() + 0.5);
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);

        double max = Math.max(ax, Math.max(ay, az));
        if (max < 0.25) return clickedFace;
        if (max == ax) return dx > 0 ? Direction.EAST : Direction.WEST;
        if (max == ay) return dy > 0 ? Direction.UP : Direction.DOWN;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
