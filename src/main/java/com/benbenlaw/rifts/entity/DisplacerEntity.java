package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public class DisplacerEntity extends ThrowableItemProjectile {

    public DisplacerEntity(EntityType<? extends DisplacerEntity> type, Level level) {
        super(type, level);
    }

    public DisplacerEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(EpochopolisEntities.DISPLACER.get(), x, y, z, level, stack);
    }

    public DisplacerEntity(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack stack) {
        super(EpochopolisEntities.DISPLACER.get(), livingEntity, serverLevel, stack);
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult result) {
        super.onHitBlock(result);

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        DisplacerConversions config = getItem().typeHolder().getData(RiftsDataMaps.DISPLACER_HIT_RESULTS);
        if (config == null || config.conversions().isEmpty()) {
            this.discard();
            return;
        }

        BlockPos center = result.getBlockPos();
        int r = config.radius();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-r, -r, -r),
                center.offset(r, r, r))) {

            if (pos.distSqr(center) > (double) r * r) {
                continue;
            }

            BlockState state = serverLevel.getBlockState(pos);
            Block replacement = config.conversions().get(state.getBlock());
            if (replacement == null) {
                continue;
            }

            if (serverLevel.getRandom().nextFloat() >= config.chance()) {
                continue;
            }

            serverLevel.setBlockAndUpdate(pos.immutable(), replacement.defaultBlockState());
        }

        this.discard();
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult result) {
        super.onHitEntity(result);

        if (level() instanceof ServerLevel serverLevel && result.getEntity() instanceof Mob target) {
            DisplacerConversions config = getItem().typeHolder().getData(RiftsDataMaps.DISPLACER_HIT_RESULTS);
            EntityType<?> replacement = config == null ? null : config.entityConversions().get(target.getType());
            if (replacement != null) {
                convert(serverLevel, target, (EntityType<? extends Mob>) replacement);
            }
        }

        this.discard();
    }

    private static <T extends Mob> void convert(ServerLevel level, Mob target, EntityType<T> replacement) {
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() / 2;
        double z = target.getZ();

        T converted = target.convertTo(replacement, ConversionParams.single(target, false, false), mob -> {});
        if (converted != null) {
            level.sendParticles(ParticleTypes.PORTAL, x, y, z, 40, 0.5, 0.8, 0.5, 0.3);
            level.playSound(null, x, y, z, SoundEvents.PORTAL_TRAVEL, SoundSource.HOSTILE, 0.3F, 1.5F);
        }
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return RiftsItems.DISPLACER.get();
    }
}