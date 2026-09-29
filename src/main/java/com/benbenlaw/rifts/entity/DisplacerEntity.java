package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.EpochopolisDataMaps;
import com.benbenlaw.rifts.item.EpochopolisItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

        DisplacerConversions config = getItem().typeHolder().getData(EpochopolisDataMaps.DISPLACER_HIT_RESULTS);
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
    protected @NonNull Item getDefaultItem() {
        return EpochopolisItems.DISPLACER.get();
    }
}