package com.benbenlaw.rifts.surge;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiftSurgeManager {

    private static final Map<ResourceKey<Level>, List<RiftSurge>> ACTIVE = new HashMap<>();

    public static void start(ServerLevel level, BlockPos origin) {
        ACTIVE.computeIfAbsent(level.dimension(), key -> new ArrayList<>()).add(new RiftSurge(level, origin));
    }

    public static void tick(ServerLevel level) {
        List<RiftSurge> surges = ACTIVE.get(level.dimension());
        if (surges == null) return;

        surges.forEach(RiftSurge::tick);
        surges.removeIf(RiftSurge::isDone);
        if (surges.isEmpty()) {
            ACTIVE.remove(level.dimension());
        }
    }

    public static void onPlayerDeath(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;

        List<RiftSurge> surges = ACTIVE.get(level.dimension());
        if (surges == null) return;
        for (RiftSurge surge : List.copyOf(surges)) {
            if (surge.isInRange(player)) {
                surge.fail(player);
            }
        }
    }

    public static void clear() {
        ACTIVE.values().forEach(list -> list.forEach(RiftSurge::end));
        ACTIVE.clear();
    }
}
