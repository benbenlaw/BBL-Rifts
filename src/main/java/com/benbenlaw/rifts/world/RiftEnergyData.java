package com.benbenlaw.rifts.world;

import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.datamaps.RiftEnergyModifier;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.entity.RiftElemental;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RiftEnergyData extends SavedData {

    private static final double NOISE_SCALE = 1.0 / 7.0;

    public record ChunkEntry(long chunk, double amount, long lastUpdate, boolean elementalSpawned) {
        static final Codec<ChunkEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.LONG.fieldOf("chunk").forGetter(ChunkEntry::chunk),
                Codec.DOUBLE.fieldOf("amount").forGetter(ChunkEntry::amount),
                Codec.LONG.fieldOf("last_update").forGetter(ChunkEntry::lastUpdate),
                Codec.BOOL.optionalFieldOf("elemental_spawned", false).forGetter(ChunkEntry::elementalSpawned)
        ).apply(i, ChunkEntry::new));
    }

    public record Reading(int current, int capacity, float richness) {}

    private static final Codec<RiftEnergyData> CODEC = ChunkEntry.CODEC.listOf()
            .fieldOf("chunks")
            .codec()
            .xmap(RiftEnergyData::unpack, RiftEnergyData::pack);

    public static final SavedDataType<RiftEnergyData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("chronopolis", "rift_energy"),
            RiftEnergyData::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private final Long2ObjectOpenHashMap<ChunkEntry> chunks = new Long2ObjectOpenHashMap<>();
    private final Long2IntOpenHashMap capacityCache = new Long2IntOpenHashMap();
    private ImprovedNoise broad;
    private ImprovedNoise fine;

    private RiftEnergyData() {
    }

    private static RiftEnergyData unpack(List<ChunkEntry> list) {
        RiftEnergyData result = new RiftEnergyData();
        for (ChunkEntry entry : list) {
            result.chunks.put(entry.chunk(), entry);
        }
        return result;
    }

    private List<ChunkEntry> pack() {
        return List.copyOf(chunks.values());
    }

    public static RiftEnergyData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private void ensureNoise(ServerLevel level) {
        if (broad != null) return;
        long seed = level.getSeed() ^ ((long) level.dimension().identifier().hashCode() * 0x9E3779B97F4A7C15L);
        RandomSource random = RandomSource.create(seed);
        broad = new ImprovedNoise(random);
        fine = new ImprovedNoise(random);
    }

    private float noiseFactor(ServerLevel level, ChunkPos pos) {
        ensureNoise(level);
        double x = pos.x() * NOISE_SCALE;
        double z = pos.z() * NOISE_SCALE;
        double n = broad.noise(x, 0, z) * 0.7 + fine.noise(x * 2.7 + 100, 0, z * 2.7 + 100) * 0.3;
        n = Mth.clamp(n * 1.6, -1.0, 1.0);
        return (float) (1.0 + n * RiftsStartupConfig.CHUNK_NOISE_VARIATION.get());
    }

    // Uncached noise biome so sampling neighbours never loads or generates their chunks
    private float biomeMultiplier(ServerLevel level, ChunkPos pos) {
        var biome = level.getUncachedNoiseBiome(QuartPos.fromBlock(pos.getMiddleBlockX()), QuartPos.fromBlock(level.getSeaLevel()), QuartPos.fromBlock(pos.getMiddleBlockZ()));
        RiftEnergyModifier modifier = biome.getData(RiftsDataMaps.BIOME_RIFT_ENERGY);
        return modifier == null ? 1.0f : modifier.multiplier();
    }

    private double rawCapacity(ServerLevel level, ChunkPos pos) {
        return RiftsStartupConfig.CHUNK_BASE_RIFT_ENERGY.get() * (double) noiseFactor(level, pos) * biomeMultiplier(level, pos);
    }

    private int capacity(ServerLevel level, ChunkPos pos) {
        long key = pos.pack();
        if (capacityCache.containsKey(key)) {
            return capacityCache.get(key);
        }

        double neighbourSum = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    neighbourSum += rawCapacity(level, new ChunkPos(pos.x() + dx, pos.z() + dz));
                }
            }
        }

        double blend = RiftsStartupConfig.CHUNK_NEIGHBOUR_BLEND.get();
        double blended = rawCapacity(level, pos) * (1.0 - blend) + (neighbourSum / 8.0) * blend;
        int capacity = (int) Mth.clamp(blended, 0, Integer.MAX_VALUE);
        capacityCache.put(key, capacity);
        return capacity;
    }

    private double currentAmount(ChunkEntry entry, int capacity, long now) {
        if (entry == null) return capacity;
        double regen = capacity * (double) Math.max(0, now - entry.lastUpdate())
                / RiftsStartupConfig.CHUNK_REGEN_TICKS_TO_FULL.get();
        return Math.min(capacity, entry.amount() + regen);
    }

    public Reading read(ServerLevel level, ChunkPos pos) {
        int capacity = capacity(level, pos);
        double current = currentAmount(chunks.get(pos.pack()), capacity, level.getGameTime());
        int base = RiftsStartupConfig.CHUNK_BASE_RIFT_ENERGY.get();
        float richness = base <= 0 ? 0 : (float) capacity / base;
        return new Reading((int) current, capacity, richness);
    }

    public int draw(ServerLevel level, BlockPos source, int requested) {
        if (requested <= 0) return 0;
        ChunkPos pos = ChunkPos.containing(source);
        long key = pos.pack();
        int capacity = capacity(level, pos);
        if (capacity <= 0) return 0;

        long now = level.getGameTime();
        ChunkEntry entry = chunks.get(key);
        double current = currentAmount(entry, capacity, now);

        int taken = (int) Math.min(requested, Math.floor(current));
        double remaining = current - taken;

        double threshold = RiftsStartupConfig.ELEMENTAL_DEPLETION_THRESHOLD.get();
        boolean spawned = entry != null && entry.elementalSpawned() && current < capacity * Math.min(1.0, threshold * 2);
        boolean summon = false;
        if (remaining < Math.max(1.0, capacity * threshold) && !spawned && RiftsStartupConfig.ELEMENTAL_ON_DEPLETION.get()) {
            spawned = true;
            summon = true;
        }

        if (taken > 0 || summon || (entry != null && entry.elementalSpawned() != spawned)) {
            chunks.put(key, new ChunkEntry(key, remaining, now, spawned));
            setDirty();
        }

        if (summon) {
            spawnElemental(level, source);
        }
        return Math.max(taken, 0);
    }

    private void spawnElemental(ServerLevel level, BlockPos origin) {
        RandomSource random = level.getRandom();
        for (int attempt = 0; attempt < 24; attempt++) {
            int x = origin.getX() + random.nextIntBetweenInclusive(-8, 8);
            int z = origin.getZ() + random.nextIntBetweenInclusive(-8, 8);
            if (Math.abs(x - origin.getX()) < 3 && Math.abs(z - origin.getZ()) < 3) continue;

            for (int y = origin.getY() + 3; y >= origin.getY() - 4; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (!level.isLoaded(pos)) break;
                if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                        && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                        && level.getBlockState(pos.above(2)).getCollisionShape(level, pos.above(2)).isEmpty()
                        && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                    place(level, pos);
                    return;
                }
            }
        }
        place(level, origin.above());
    }

    private void place(ServerLevel level, BlockPos pos) {
        RiftElemental elemental = EpochopolisEntities.RIFT_ELEMENTAL.get().spawn(level, pos, EntitySpawnReason.EVENT);
        if (elemental != null) {
            elemental.setPersistenceRequired();
        }
    }

    // Moves energy from fuller chunks to emptier orthogonal neighbours. Only chunks with a saved entry (plus their
    // neighbours) take part, so cost scales with the drained area, and chunks that end up full drop their entry.
    public void tickDiffusion(ServerLevel level) {
        double rate = RiftsStartupConfig.CHUNK_DIFFUSION_RATE.get();
        if (rate <= 0 || chunks.isEmpty()) return;

        long now = level.getGameTime();
        List<Long> active = new ArrayList<>(chunks.keySet());
        Set<Long> activeSet = new HashSet<>(active);

        Long2DoubleOpenHashMap current = new Long2DoubleOpenHashMap();
        Long2IntOpenHashMap capacities = new Long2IntOpenHashMap();
        for (long key : active) {
            for (int i = 0; i < 5; i++) {
                long involved = i == 0 ? key : neighbour(key, i - 1);
                if (capacities.containsKey(involved)) continue;
                int capacity = capacity(level, ChunkPos.unpack(involved));
                capacities.put(involved, capacity);
                current.put(involved, currentAmount(chunks.get(involved), capacity, now));
            }
        }

        Long2DoubleOpenHashMap delta = new Long2DoubleOpenHashMap();
        for (long key : active) {
            int capacity = capacities.get(key);
            if (capacity <= 0) continue;

            for (int i = 0; i < 4; i++) {
                long other = neighbour(key, i);
                int otherCapacity = capacities.get(other);
                if (otherCapacity <= 0) continue;
                if (activeSet.contains(other) && other < key) continue;

                double fraction = current.get(key) / capacity;
                double otherFraction = current.get(other) / otherCapacity;
                double difference = otherFraction - fraction;
                if (Math.abs(difference) < 1.0E-6) continue;

                long source = difference > 0 ? other : key;
                long sink = difference > 0 ? key : other;
                double amount = Math.min(Math.abs(difference) * Math.min(capacity, otherCapacity) * rate, current.get(source));
                delta.addTo(source, -amount);
                delta.addTo(sink, amount);
            }
        }

        boolean changed = false;
        for (long key : capacities.keySet()) {
            int capacity = capacities.get(key);
            double updated = Mth.clamp(current.get(key) + delta.get(key), 0, capacity);
            ChunkEntry entry = chunks.get(key);

            if (updated >= capacity - 0.5) {
                if (entry != null) {
                    chunks.remove(key);
                    changed = true;
                }
            } else if (entry != null || delta.containsKey(key)) {
                chunks.put(key, new ChunkEntry(key, updated, now, entry != null && entry.elementalSpawned()));
                changed = true;
            }
        }

        if (changed) {
            setDirty();
        }
    }

    private static long neighbour(long key, int index) {
        ChunkPos pos = ChunkPos.unpack(key);
        return switch (index) {
            case 0 -> new ChunkPos(pos.x() + 1, pos.z()).pack();
            case 1 -> new ChunkPos(pos.x() - 1, pos.z()).pack();
            case 2 -> new ChunkPos(pos.x(), pos.z() + 1).pack();
            default -> new ChunkPos(pos.x(), pos.z() - 1).pack();
        };
    }
}
