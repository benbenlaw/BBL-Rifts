package com.benbenlaw.rifts.block.accelerator;

import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.util.EpochopolisTags;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class TickAcceleration {

    private static final Map<ResourceKey<Level>, Long2IntOpenHashMap> USED = new HashMap<>();
    private static final Map<ResourceKey<Level>, Long> USED_TICK = new HashMap<>();

    public static void clear() {
        USED.clear();
        USED_TICK.clear();
    }

    public static boolean isTarget(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(EpochopolisTags.Blocks.TICK_ACCELERATOR_BLACKLIST)) return false;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && ticker(level, state, blockEntity) != null) return true;

        return state.isRandomlyTicking();
    }

    // Accelerators touching the same block add their extra ticks together, up to a per-tick cap
    public static int reserve(ServerLevel level, BlockPos pos, int requested) {
        ResourceKey<Level> dimension = level.dimension();
        long now = level.getGameTime();

        Long2IntOpenHashMap used = USED.computeIfAbsent(dimension, key -> new Long2IntOpenHashMap());
        if (!Long.valueOf(now).equals(USED_TICK.get(dimension))) {
            used.clear();
            USED_TICK.put(dimension, now);
        }

        int alreadyUsed = used.get(pos.asLong());
        int granted = Math.max(0, Math.min(requested, RiftsStartupConfig.ACCELERATOR_MAX_EXTRA_TICKS.get() - alreadyUsed));
        used.put(pos.asLong(), alreadyUsed + granted);
        return granted;
    }

    public static void accelerate(ServerLevel level, BlockPos pos, int extraTicks) {
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            tickBlockEntity(level, pos, state, blockEntity, extraTicks);
        }

        double expected = extraTicks * RiftsStartupConfig.ACCELERATOR_RANDOM_TICKS_PER_EXTRA_TICK.get();
        int randomTicks = (int) expected;
        if (level.getRandom().nextDouble() < expected - randomTicks) {
            randomTicks++;
        }

        for (int i = 0; i < randomTicks; i++) {
            state = level.getBlockState(pos);
            if (!state.isRandomlyTicking()) break;
            state.randomTick(level, pos, level.getRandom());
        }
    }

    private static <T extends BlockEntity> void tickBlockEntity(ServerLevel level, BlockPos pos, BlockState state, T blockEntity, int times) {
        BlockEntityTicker<T> ticker = ticker(level, state, blockEntity);
        if (ticker == null) return;

        for (int i = 0; i < times && !blockEntity.isRemoved(); i++) {
            ticker.tick(level, pos, state, blockEntity);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityTicker<T> ticker(Level level, BlockState state, T blockEntity) {
        return state.getTicker(level, (BlockEntityType<T>) blockEntity.getType());
    }
}
