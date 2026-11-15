package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.particle.RiftParticleEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RiftPortal extends Entity {

    private static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(RiftPortal.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Direction> LONG_AXIS = SynchedEntityData.defineId(RiftPortal.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(RiftPortal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SLOT = SynchedEntityData.defineId(RiftPortal.class, EntityDataSerializers.INT);

    private static final double HALF_LONG = 1.0;
    private static final double HALF_SHORT = 0.5;
    private static final double REACH = 0.6;
    private static final double MIN_EXIT_SPEED = 0.15;
    private static final int TRAVEL_COOLDOWN = 8;

    private static final Map<UUID, Integer> COOLDOWNS = new HashMap<>();

    private @Nullable UUID gunId;
    private long created;

    public RiftPortal(EntityType<? extends RiftPortal> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public Direction facing() {
        return entityData.get(FACING);
    }

    public Direction longAxis() {
        return entityData.get(LONG_AXIS);
    }

    public DyeColor color() {
        return DyeColor.byId(entityData.get(COLOR));
    }

    public int slot() {
        return entityData.get(SLOT);
    }

    private Vec3 normal() {
        return vec(facing());
    }

    private Vec3 up() {
        return vec(longAxis());
    }

    private Vec3 right() {
        Vec3 up = up();
        Vec3 n = normal();
        return new Vec3(up.y * n.z - up.z * n.y, up.z * n.x - up.x * n.z, up.x * n.y - up.y * n.x);
    }

    private static Vec3 vec(Direction direction) {
        return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    @Override
    protected @NotNull AABB makeBoundingBox(@NotNull Vec3 position) {
        Vec3 n = vec(entityData.get(FACING));
        Vec3 up = vec(entityData.get(LONG_AXIS));
        Vec3 right = new Vec3(up.y * n.z - up.z * n.y, up.z * n.x - up.x * n.z, up.x * n.y - up.y * n.x);
        Vec3 centre = position.add(n.scale(REACH / 2));
        Vec3 half = new Vec3(
                Math.abs(up.x) * HALF_LONG + Math.abs(right.x) * HALF_SHORT + Math.abs(n.x) * REACH / 2,
                Math.abs(up.y) * HALF_LONG + Math.abs(right.y) * HALF_SHORT + Math.abs(n.y) * REACH / 2,
                Math.abs(up.z) * HALF_LONG + Math.abs(right.z) * HALF_SHORT + Math.abs(n.z) * REACH / 2);
        return new AABB(centre.subtract(half), centre.add(half));
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(FACING) || accessor.equals(LONG_AXIS)) {
            setBoundingBox(makeBoundingBox());
        }
    }

    public static boolean place(ServerLevel level, ServerPlayer player, UUID gunId, int slot, DyeColor color, BlockHitResult hit) {
        Direction face = hit.getDirection();
        BlockPos pos = hit.getBlockPos();
        Vec3 relative = hit.getLocation().subtract(Vec3.atCenterOf(pos));

        Direction longAxis;
        Direction preferred;
        if (face.getAxis().isHorizontal()) {
            longAxis = Direction.UP;
            preferred = relative.y >= 0 ? Direction.UP : Direction.DOWN;
        } else {
            longAxis = player.getDirection();
            preferred = vec(longAxis).dot(relative) >= 0 ? longAxis : longAxis.getOpposite();
        }

        for (Direction extra : new Direction[]{preferred, preferred.getOpposite()}) {
            BlockPos second = pos.relative(extra);
            if (!fits(level, pos, face) || !fits(level, second, face)) continue;

            Vec3 centre = Vec3.atCenterOf(pos).add(vec(face).scale(0.51)).add(vec(extra).scale(0.5));
            RiftPortal portal = RiftsEntities.RIFT_PORTAL.get().create(level, EntitySpawnReason.TRIGGERED);
            if (portal == null) return false;

            portal.entityData.set(FACING, face);
            portal.entityData.set(LONG_AXIS, longAxis);
            portal.entityData.set(SLOT, slot);
            portal.entityData.set(COLOR, color.getId());
            portal.gunId = gunId;
            portal.created = level.getServer().overworld().getGameTime();
            portal.setPos(centre);

            RiftPortal other = find(level.getServer(), gunId, 1 - slot);
            if (other != null && other.level() == level && other.getBoundingBox().intersects(portal.getBoundingBox())) {
                portal.discard();
                continue;
            }

            removeExisting(level.getServer(), gunId, slot);
            level.addFreshEntity(portal);
            RiftParticleEffects.burst(level, centre.x, centre.y, centre.z, 12, 0.5);
            return true;
        }
        return false;
    }

    private static boolean fits(ServerLevel level, BlockPos pos, Direction face) {
        BlockState state = level.getBlockState(pos);
        if (!state.isFaceSturdy(level, pos, face)) return false;
        BlockPos front = pos.relative(face);
        return level.getBlockState(front).getCollisionShape(level, front).isEmpty() && level.getFluidState(front).isEmpty();
    }

    public static void removeExisting(MinecraftServer server, UUID gunId, int slot) {
        for (ServerLevel level : server.getAllLevels()) {
            for (RiftPortal portal : level.getEntities(EntityTypeTest.forClass(RiftPortal.class), p -> gunId.equals(p.gunId) && p.slot() == slot)) {
                portal.discard();
            }
        }
    }

    public static @Nullable RiftPortal find(MinecraftServer server, UUID gunId, int slot) {
        RiftPortal best = null;
        for (ServerLevel level : server.getAllLevels()) {
            for (RiftPortal portal : level.getEntities(EntityTypeTest.forClass(RiftPortal.class), p -> gunId.equals(p.gunId) && p.slot() == slot && p.isAlive())) {
                if (best == null || portal.created > best.created) best = portal;
            }
        }
        return best;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel serverLevel) || gunId == null) return;

        if (tickCount % 10 == 0 && !supported(serverLevel)) {
            RiftParticleEffects.burst(serverLevel, getX(), getY(), getZ(), 12, 0.5);
            discard();
            return;
        }

        if (tickCount % 40 == 0) {
            RiftPortal newest = find(serverLevel.getServer(), gunId, slot());
            if (newest != null && newest != this && newest.created > created) {
                discard();
                return;
            }
        }

        if (tickCount % 4 == 0) {
            Vec3 spot = position().add(right().scale((random.nextDouble() - 0.5) * 0.8)).add(up().scale((random.nextDouble() - 0.5) * 1.6)).add(normal().scale(0.1));
            RiftParticleEffects.absorb(serverLevel, spot.x, spot.y, spot.z, 1, 0.2, 0.2, 0.2);
        }

        int now = serverLevel.getServer().getTickCount();
        for (Entity entity : serverLevel.getEntities(this, getBoundingBox(), RiftPortal::canTravel)) {
            Integer until = COOLDOWNS.get(entity.getUUID());
            if (until != null && until > now) continue;
            if (!approaching(entity)) continue;

            RiftPortal partner = find(serverLevel.getServer(), gunId, 1 - slot());
            if (partner == null || !(partner.level() instanceof ServerLevel partnerLevel)) return;

            travel(entity, partner, partnerLevel);
            COOLDOWNS.put(entity.getUUID(), now + TRAVEL_COOLDOWN);
        }
        if (tickCount % 200 == 0) {
            COOLDOWNS.values().removeIf(until -> until <= now);
        }
    }

    private static boolean canTravel(Entity entity) {
        return !(entity instanceof RiftPortal) && !(entity instanceof NaturalRift) && entity.isAlive() && !entity.isSpectator()
                && !entity.isPassenger() && !entity.isVehicle() && entity.getBbWidth() <= 0.95F && entity.getBbHeight() <= 1.95F;
    }

    private boolean approaching(Entity entity) {
        Vec3 n = normal();
        Vec3 moved = entity.position().subtract(entity.xo, entity.yo, entity.zo);
        Vec3 motion = entity instanceof ServerPlayer player ? player.getKnownMovement() : entity.getDeltaMovement();
        return moved.dot(n) < -0.002 || motion.dot(n) < -0.002;
    }

    private boolean supported(ServerLevel level) {
        Vec3 behind = position().subtract(normal().scale(0.5));
        for (double side : new double[]{0.5, -0.5}) {
            BlockPos pos = BlockPos.containing(behind.add(up().scale(side)));
            if (!level.getBlockState(pos).isFaceSturdy(level, pos, facing())) return false;
        }
        return true;
    }

    private void travel(Entity entity, RiftPortal exit, ServerLevel exitLevel) {
        Vec3 centre = entity.getBoundingBox().getCenter().subtract(position());
        Vec3 velocity = entity instanceof ServerPlayer player && player.getKnownMovement().lengthSqr() > entity.getDeltaMovement().lengthSqr()
                ? player.getKnownMovement() : entity.getDeltaMovement();
        Vec3 look = entity.getLookAngle();

        Vec3 rightIn = right();
        Vec3 upIn = up();
        Vec3 nIn = normal();
        Vec3 rightOut = exit.right();
        Vec3 upOut = exit.up();
        Vec3 nOut = exit.normal();

        double limitU = Math.max(0, HALF_LONG - extent(upOut, entity));
        double limitR = Math.max(0, HALF_SHORT - extent(rightOut, entity));
        double u = Mth.clamp(centre.dot(upIn), -limitU, limitU);
        double r = Mth.clamp(-centre.dot(rightIn), -limitR, limitR);

        Vec3 newCentre = exit.position().add(rightOut.scale(r)).add(upOut.scale(u)).add(nOut.scale(extent(nOut, entity) + 0.06));
        Vec3 newPos = newCentre.subtract(0, entity.getBbHeight() / 2.0, 0);

        Vec3 newVelocity = rightOut.scale(-velocity.dot(rightIn)).add(upOut.scale(velocity.dot(upIn))).add(nOut.scale(-velocity.dot(nIn)));
        double outward = newVelocity.dot(nOut);
        if (outward < MIN_EXIT_SPEED) {
            newVelocity = newVelocity.add(nOut.scale(MIN_EXIT_SPEED - outward));
        }

        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        if (entity instanceof LivingEntity) {
            Vec3 newLook = rightOut.scale(-look.dot(rightIn)).add(upOut.scale(look.dot(upIn))).add(nOut.scale(-look.dot(nIn)));
            double horizontal = Math.sqrt(newLook.x * newLook.x + newLook.z * newLook.z);
            yaw = Mth.wrapDegrees((float) (Mth.atan2(-newLook.x, newLook.z) * 180.0 / Math.PI));
            pitch = (float) (-(Mth.atan2(newLook.y, horizontal) * 180.0 / Math.PI));
        }

        level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.4F, 1.4F);
        entity.teleport(new TeleportTransition(exitLevel, newPos, newVelocity, yaw, pitch, TeleportTransition.DO_NOTHING));
        exitLevel.playSound(null, newPos.x, newPos.y, newPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.4F, 1.6F);
        if (entity instanceof Player player) {
            player.fallDistance = 0;
        }
    }

    private static double extent(Vec3 axis, Entity entity) {
        return Math.abs(axis.y) > 0.5 ? entity.getBbHeight() / 2.0 : entity.getBbWidth() / 2.0;
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
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(FACING, Direction.NORTH);
        builder.define(LONG_AXIS, Direction.UP);
        builder.define(SLOT, 0);
        builder.define(COLOR, DyeColor.BLUE.getId());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        entityData.set(FACING, Direction.from3DDataValue(input.getIntOr("facing", Direction.NORTH.get3DDataValue())));
        entityData.set(LONG_AXIS, Direction.from3DDataValue(input.getIntOr("longAxis", Direction.UP.get3DDataValue())));
        entityData.set(SLOT, input.getIntOr("slot", 0));
        entityData.set(COLOR, input.getIntOr("color", DyeColor.BLUE.getId()));
        gunId = input.read("gun", UUIDUtil.CODEC).orElse(null);
        created = input.getLongOr("created", 0L);
        setBoundingBox(makeBoundingBox());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        output.putInt("facing", facing().get3DDataValue());
        output.putInt("longAxis", longAxis().get3DDataValue());
        output.putInt("slot", slot());
        output.putInt("color", color().getId());
        output.storeNullable("gun", UUIDUtil.CODEC, gunId);
        output.putLong("created", created);
    }
}
