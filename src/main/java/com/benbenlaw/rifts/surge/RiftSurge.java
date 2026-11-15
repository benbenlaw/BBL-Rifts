package com.benbenlaw.rifts.surge;

import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.data.RiftsLootTables;
import com.benbenlaw.rifts.entity.RiftsEntities;
import com.benbenlaw.rifts.entity.NaturalRift;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RiftSurge {

    private static final double PLAYER_RANGE = 96.0;
    private static final int COUNTDOWN_TICKS = 100;
    private static final int BETWEEN_WAVES_TICKS = 100;
    private static final int ABANDON_TICKS = 1200;
    private static final int RIFT_LIFETIME = 400;
    private static final int MIN_RIFT_DISTANCE = 8;
    private static final int MAX_RIFT_DISTANCE = 16;

    private static final List<EntityType<? extends Mob>> RAIDERS = List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER);

    private enum Phase { COUNTDOWN, ACTIVE, BETWEEN_WAVES, DONE }

    private final ServerLevel level;
    private final BlockPos origin;
    private final int totalWaves;
    private final ServerBossEvent bossBar = new ServerBossEvent(UUID.randomUUID(), Component.empty(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_6);
    private final Set<UUID> mobs = new HashSet<>();

    private Phase phase = Phase.COUNTDOWN;
    private int wave = 0;
    private int waveSize = 1;
    private int timer = COUNTDOWN_TICKS;
    private int unattendedTicks = 0;

    public RiftSurge(ServerLevel level, BlockPos origin) {
        this.level = level;
        this.origin = origin;
        this.totalWaves = Math.max(1, RiftsStartupConfig.SURGE_WAVES.get());
        updateBar(0.0F);
    }

    public boolean isDone() {
        return phase == Phase.DONE;
    }

    public void tick() {
        List<ServerPlayer> nearby = level.players().stream()
                .filter(player -> !player.isSpectator() && player.position().distanceToSqr(Vec3.atCenterOf(origin)) <= PLAYER_RANGE * PLAYER_RANGE)
                .toList();
        syncBossBar(nearby);

        unattendedTicks = nearby.stream().anyMatch(ServerPlayer::isAlive) ? 0 : unattendedTicks + 1;
        if (unattendedTicks > ABANDON_TICKS) {
            abandon();
            return;
        }

        switch (phase) {
            case COUNTDOWN, BETWEEN_WAVES -> {
                if (--timer <= 0) {
                    spawnWave(nearby);
                    phase = Phase.ACTIVE;
                }
            }
            case ACTIVE -> {
                mobs.removeIf(id -> {
                    Entity entity = level.getEntity(id);
                    return entity == null || !entity.isAlive();
                });
                updateBar(waveSize <= 0 ? 0.0F : (float) mobs.size() / waveSize);

                if (mobs.isEmpty()) {
                    if (wave + 1 >= totalWaves) {
                        victory(nearby);
                    } else {
                        wave++;
                        timer = BETWEEN_WAVES_TICKS;
                        phase = Phase.BETWEEN_WAVES;
                    }
                }
            }
            case DONE -> { }
        }
    }

    private void spawnWave(List<ServerPlayer> nearby) {
        RandomSource random = level.getRandom();
        int raiders = 3 + wave * 2;
        boolean lastWave = wave + 1 >= totalWaves;
        int elementals = lastWave ? (totalWaves >= 5 ? 2 : 1) : 0;
        int rifts = Math.min(2 + wave, 5);

        List<Vec3> riftSpots = new ArrayList<>();
        for (int i = 0; i < rifts; i++) {
            BlockPos spot = findSurface(random);
            riftSpots.add(Vec3.atBottomCenterOf(spot));

            NaturalRift rift = RiftsEntities.NATURAL_RIFT.get().create(level, EntitySpawnReason.EVENT);
            if (rift != null) {
                rift.setPos(spot.getX() + 0.5, spot.getY(), spot.getZ() + 0.5);
                rift.setDecorative(true);
                rift.setLifetime(RIFT_LIFETIME);
                level.addFreshEntity(rift);
            }
        }

        int spawned = 0;
        for (int i = 0; i < raiders; i++) {
            spawned += spawnMob(RAIDERS.get(random.nextInt(RAIDERS.size())), riftSpots.get(i % riftSpots.size()), nearby) ? 1 : 0;
        }
        for (int i = 0; i < elementals; i++) {
            spawned += spawnMob(RiftsEntities.RIFT_ELEMENTAL.get(), riftSpots.get(i % riftSpots.size()), nearby) ? 1 : 0;
        }

        waveSize = Math.max(1, spawned);
        level.playSound(null, origin, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 0.6F, 1.4F);
        updateBar(1.0F);
    }

    private boolean spawnMob(EntityType<? extends Mob> type, Vec3 at, List<ServerPlayer> nearby) {
        BlockPos pos = BlockPos.containing(at.x + level.getRandom().nextInt(3) - 1, at.y, at.z + level.getRandom().nextInt(3) - 1);
        Mob mob = type.spawn(level, pos, EntitySpawnReason.EVENT);
        if (mob == null) return false;

        mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        nearby.stream().min((a, b) -> Double.compare(a.distanceToSqr(mob), b.distanceToSqr(mob))).ifPresent(mob::setTarget);

        RiftParticleEffects.burst(level, mob.getX(), mob.getY() + 1.0, mob.getZ(), 12, 0.4);
        mobs.add(mob.getUUID());
        return true;
    }

    private BlockPos findSurface(RandomSource random) {
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = MIN_RIFT_DISTANCE + random.nextDouble() * (MAX_RIFT_DISTANCE - MIN_RIFT_DISTANCE);
            int x = origin.getX() + (int) Math.round(Math.cos(angle) * distance);
            int z = origin.getZ() + (int) Math.round(Math.sin(angle) * distance);

            if (!level.hasChunkAt(new BlockPos(x, origin.getY(), z))) continue;
            BlockPos feet = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
            if (level.getBlockState(feet.below()).blocksMotion() && level.getFluidState(feet).isEmpty()) return feet;
        }
        return origin;
    }

    private void victory(List<ServerPlayer> nearby) {
        phase = Phase.DONE;
        bossBar.removeAllPlayers();

        LootTable table = level.getServer().reloadableRegistries().getLootTable(RiftsLootTables.SURGE_REWARD);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(origin))
                .create(LootContextParamSets.CHEST);
        for (ItemStack stack : table.getRandomItems(params)) {
            level.addFreshEntity(new ItemEntity(level, origin.getX() + 0.5, origin.getY() + 1.0, origin.getZ() + 0.5, stack));
        }

        RiftParticleEffects.burst(level, origin.getX() + 0.5, origin.getY() + 1.0, origin.getZ() + 0.5, 60, 1.0);
        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
        for (ServerPlayer player : nearby) {
            player.sendOverlayMessage(Component.translatable("message.rifts.surge_cleared"));
        }
    }

    public boolean isInRange(ServerPlayer player) {
        return player.position().distanceToSqr(Vec3.atCenterOf(origin)) <= PLAYER_RANGE * PLAYER_RANGE;
    }

    public void fail(ServerPlayer fallen) {
        if (phase == Phase.DONE) return;

        for (UUID id : mobs) {
            Entity entity = level.getEntity(id);
            if (entity != null && entity.isAlive()) {
                RiftParticleEffects.burst(level, entity.getX(), entity.getY() + 1.0, entity.getZ(), 20, 0.5);
                entity.kill(level);
            }
        }
        mobs.clear();

        for (ServerPlayer player : level.players()) {
            if (player != fallen && !player.isSpectator() && isInRange(player)) {
                player.sendOverlayMessage(Component.translatable("message.rifts.surge_failed", fallen.getDisplayName()));
            }
        }
        level.playSound(null, origin, SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 0.5F, 1.6F);
        end();
    }

    private void abandon() {
        for (UUID id : mobs) {
            Entity entity = level.getEntity(id);
            if (entity != null) {
                RiftParticleEffects.burst(level, entity.getX(), entity.getY() + 1.0, entity.getZ(), 10, 0.4);
                entity.discard();
            }
        }
        end();
    }

    public void end() {
        phase = Phase.DONE;
        bossBar.removeAllPlayers();
    }

    private void syncBossBar(List<ServerPlayer> nearby) {
        for (ServerPlayer player : nearby) {
            bossBar.addPlayer(player);
        }
        for (ServerPlayer player : new ArrayList<>(bossBar.getPlayers())) {
            if (!nearby.contains(player)) {
                bossBar.removePlayer(player);
            }
        }
        if (phase == Phase.COUNTDOWN || phase == Phase.BETWEEN_WAVES) {
            bossBar.setName(Component.translatable("message.rifts.surge_incoming", Math.min(wave + 1, totalWaves), totalWaves));
        } else {
            bossBar.setName(Component.translatable("message.rifts.surge_wave", wave + 1, totalWaves));
        }
    }

    private void updateBar(float progress) {
        bossBar.setProgress(Math.max(0.0F, Math.min(1.0F, progress)));
    }
}
