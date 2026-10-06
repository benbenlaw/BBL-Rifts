package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.util.EpochopolisTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NaturalRift extends Entity {

    public static final int PERMANENT = -1;

    private static final int PLATFORM_Y = 64;
    private static final int PLATFORM_RADIUS = 3;

    private int lifetime = PERMANENT;
    private @Nullable GlobalPos destination;
    private @Nullable GlobalPos linkedArrival;

    // Players who just travelled stay ignored by every rift until they have stood clear of all of them for a second
    private static final Map<UUID, Integer> BLOCKED_UNTIL = new HashMap<>();
    private static final int TRAVEL_COOLDOWN = 40;
    private static final int CLEAR_DELAY = 20;

    public NaturalRift(EntityType<? extends NaturalRift> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public void setLifetime(int ticks) {
        this.lifetime = ticks;
    }

    public void setDestination(GlobalPos destination) {
        this.destination = destination;
    }

    @Override
    public void tick() {
        super.tick();

        if (!(level() instanceof ServerLevel serverLevel)) return;

        if (lifetime != PERMANENT && lifetime-- <= 0) {
            RiftParticleEffects.burst(serverLevel, getX(), getY() + 1.2, getZ(), 40, 0.6);
            discard();
            return;
        }

        List<ServerPlayer> players = serverLevel.getEntitiesOfClass(ServerPlayer.class, getBoundingBox(), player -> !player.isSpectator());
        int now = serverLevel.getServer().getTickCount();
        for (ServerPlayer player : players) {
            Integer until = BLOCKED_UNTIL.get(player.getUUID());
            if (until != null && until > now) {
                BLOCKED_UNTIL.put(player.getUUID(), now + CLEAR_DELAY);
                continue;
            }
            BLOCKED_UNTIL.remove(player.getUUID());
            if (!serverLevel.dimension().equals(RiftsWorldGen.RIFT_LEVEL) && !wearsFullRiftArmor(player)) {
                player.sendOverlayMessage(Component.translatable("message.rifts.rift_needs_armor"));
                BLOCKED_UNTIL.put(player.getUUID(), now + CLEAR_DELAY);
                continue;
            }
            travel(serverLevel, player);
            BLOCKED_UNTIL.put(player.getUUID(), now + TRAVEL_COOLDOWN);
            break;
        }

        if (tickCount % 4 == 0) {
            RiftParticleEffects.absorb(serverLevel, getX(), getY() + 1.2, getZ(), 2, 0.6, 1.4, 0.8);
        }
    }

    public static boolean wearsFullRiftArmor(ServerPlayer player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (!player.getItemBySlot(slot).is(EpochopolisTags.Items.RIFT_PROTECTIVE_ARMOR)) return false;
        }
        return true;
    }

    public static void blockTravel(ServerPlayer player, net.minecraft.server.MinecraftServer server) {
        BLOCKED_UNTIL.put(player.getUUID(), server.getTickCount() + TRAVEL_COOLDOWN);
    }

    private void travel(ServerLevel from, ServerPlayer player) {
        ServerLevel target;
        Vec3 arrival;

        if (from.dimension().equals(RiftsWorldGen.RIFT_LEVEL)) {
            GlobalPos home = destination;
            target = home == null ? null : from.getServer().getLevel(home.dimension());
            if (target == null) {
                target = from.getServer().overworld();
                arrival = surfaceAt(target, getBlockX(), getBlockZ());
            } else {
                arrival = safeNear(target, home.pos());
            }
        } else {
            target = from.getServer().getLevel(RiftsWorldGen.RIFT_LEVEL);
            if (target == null) return;

            if (linkedArrival == null) {
                linkedArrival = GlobalPos.of(target.dimension(), createArrival(target, from, blockPosition()));
            }
            BlockPos pos = linkedArrival.pos();
            arrival = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            player.setData(RiftsAttachments.RIFT_ENTRANCE, GlobalPos.of(from.dimension(), blockPosition()));
        }

        RiftParticleEffects.burst(from, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.4);
        from.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.3F, 1.5F);

        player.teleportTo(target, arrival.x, arrival.y, arrival.z, Set.of(), player.getYRot(), player.getXRot(), true);

        RiftParticleEffects.burst(target, arrival.x, arrival.y + 1.0, arrival.z, 30, 0.4);
    }

    public static Vec3 surfaceAt(ServerLevel level, int x, int z) {
        return new Vec3(x + 0.5, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z + 0.5);
    }

    // Prefers the exact spot the rift stood, nudging up or down a little if that is no longer standable
    public static Vec3 safeNear(ServerLevel level, BlockPos pos) {
        level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        for (int offset = 0; offset <= 8; offset++) {
            for (int sign : new int[]{1, -1}) {
                BlockPos candidate = pos.above(sign * offset);
                if (standable(level, candidate)) {
                    return new Vec3(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                }
                if (offset == 0) break;
            }
        }
        return surfaceAt(level, pos.getX(), pos.getZ());
    }

    private static boolean standable(ServerLevel level, BlockPos feet) {
        return level.getBlockState(feet).getCollisionShape(level, feet).isEmpty() && level.getFluidState(feet).isEmpty()
                && level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()
                && !level.getBlockState(feet.below()).getCollisionShape(level, feet.below()).isEmpty();
    }

    // Uses the island surface at the same x/z when there is one, otherwise raises a small rift stone platform
    private static BlockPos createArrival(ServerLevel level, ServerLevel origin, BlockPos originPos) {
        int x = originPos.getX();
        int z = originPos.getZ();
        level.getChunk(x >> 4, z >> 4);

        BlockPos feet = BlockPos.containing(surfaceAt(level, x, z));
        boolean onGround = feet.getY() > level.getMinY() + 1 && level.getBlockState(feet.below()).blocksMotion();

        if (!onGround) {
            feet = new BlockPos(x, PLATFORM_Y + 1, z);
            for (int dx = -PLATFORM_RADIUS; dx <= PLATFORM_RADIUS; dx++) {
                for (int dz = -PLATFORM_RADIUS; dz <= PLATFORM_RADIUS; dz++) {
                    level.setBlockAndUpdate(new BlockPos(x + dx, PLATFORM_Y, z + dz), RiftsBlocks.RIFT_STONE.get().defaultBlockState());
                }
            }
        }

        for (int dy = 0; dy < 3; dy++) {
            level.setBlockAndUpdate(feet.above(dy), Blocks.AIR.defaultBlockState());
        }

        NaturalRift back = EpochopolisEntities.NATURAL_RIFT.get().create(level, EntitySpawnReason.EVENT);
        if (back != null) {
            back.setPos(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
            back.setDestination(GlobalPos.of(origin.dimension(), originPos));
            level.addFreshEntity(back);
        }
        return feet;
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float damage) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        lifetime = input.getIntOr("lifetime", PERMANENT);
        destination = input.read("destination", GlobalPos.CODEC).orElse(null);
        linkedArrival = input.read("linkedArrival", GlobalPos.CODEC).orElse(null);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        output.putInt("lifetime", lifetime);
        output.storeNullable("destination", GlobalPos.CODEC, destination);
        output.storeNullable("linkedArrival", GlobalPos.CODEC, linkedArrival);
    }
}
