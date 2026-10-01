package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.world.RiftEnergyData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class RiftScannerItem extends Item {

    public RiftScannerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(Level level, Player player, @NonNull InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            ChunkPos chunk = ChunkPos.containing(player.blockPosition());
            RiftEnergyData.Reading reading = RiftEnergyData.get(serverLevel).read(serverLevel, chunk);

            int percent = reading.capacity() <= 0 ? 0 : Math.round(100f * reading.current() / reading.capacity());
            player.sendOverlayMessage(Component.translatable("message.rifts.rift_scanner",
                    reading.current(), reading.capacity(), percent,
                    String.format("%.2f", reading.richness())));
        }
        return InteractionResult.SUCCESS;
    }
}
