package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.entity.NaturalRift;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class NaturalRiftSpawner {

    private static final int MIN_DISTANCE = 24;
    private static final int MAX_DISTANCE = 64;
    private static final double NEARBY_RADIUS = 96.0;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension().equals(RiftsWorldGen.RIFT_LEVEL) || level.dimensionType().hasCeiling()) return;
        if (!RiftsStartupConfig.NATURAL_RIFT_DIMENSIONS.get().contains(level.dimension().identifier().toString())) return;
        if (!RiftsStartupConfig.NATURAL_RIFTS_ENABLED.get()) return;
        if (level.getGameTime() % RiftsStartupConfig.NATURAL_RIFT_CHECK_INTERVAL_TICKS.get() != 0) return;
        if (level.getServer().getLevel(RiftsWorldGen.RIFT_LEVEL) == null) return;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || level.getRandom().nextDouble() >= RiftsStartupConfig.NATURAL_RIFT_SPAWN_CHANCE.get()) continue;
            trySpawnNear(level, player);
        }
    }

    private static void trySpawnNear(ServerLevel level, ServerPlayer player) {
        int nearby = level.getEntitiesOfClass(NaturalRift.class, player.getBoundingBox().inflate(NEARBY_RADIUS)).size();
        if (nearby >= RiftsStartupConfig.NATURAL_RIFT_MAX_NEARBY.get()) return;

        RandomSource random = level.getRandom();
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = MIN_DISTANCE + random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
        int x = player.getBlockX() + (int) Math.round(Math.cos(angle) * distance);
        int z = player.getBlockZ() + (int) Math.round(Math.sin(angle) * distance);

        if (!level.hasChunkAt(new BlockPos(x, 0, z))) return;

        BlockPos feet = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
        if (!level.getBlockState(feet.below()).blocksMotion() || !level.getFluidState(feet).isEmpty()
                || !level.getBlockState(feet).canBeReplaced() || !level.getBlockState(feet.above()).canBeReplaced()) return;

        NaturalRift rift = EpochopolisEntities.NATURAL_RIFT.get().create(level, EntitySpawnReason.EVENT);
        if (rift == null) return;

        rift.setPos(x + 0.5, feet.getY(), z + 0.5);
        rift.setLifetime(RiftsStartupConfig.NATURAL_RIFT_LIFETIME_TICKS.get());
        level.addFreshEntity(rift);
    }
}
