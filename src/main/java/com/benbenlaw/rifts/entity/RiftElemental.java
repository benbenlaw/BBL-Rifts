package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.particle.RiftsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class RiftElemental extends Monster {

    private int attackAnimationTick;

    public RiftElemental(EntityType<? extends RiftElemental> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 10, true, false,
                (target, level) -> !(target instanceof Enemy) && !(target instanceof RiftElemental)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            this.attackAnimationTick--;
        }
        if (this.level().isClientSide() && this.tickCount % 2 == 0) {
            spawnAbsorbParticle();
        }
    }

    private void spawnAbsorbParticle() {
        double theta = this.random.nextDouble() * Math.PI * 2;
        double distance = 1.5 + this.random.nextDouble() * 0.8;
        double dx = Math.cos(theta) * distance;
        double dz = Math.sin(theta) * distance;
        double dy = (this.random.nextDouble() - 0.5) * 2.0;

        this.level().addParticle(RiftsParticles.RIFT_ABSORB.get(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(), dx, dy, dz);
    }

    @Override
    public boolean doHurtTarget(@NonNull ServerLevel level, @NonNull Entity target) {
        this.attackAnimationTick = 10;
        level.broadcastEntityEvent(this, (byte) 4);
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource source = this.damageSources().mobAttack(this);
        boolean hurt = target.hurtServer(level, source, damage);
        if (hurt) {
            double resistance = target instanceof LivingEntity living ? living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0;
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4 * Math.max(0.0, 1.0 - resistance), 0.0));
        }
        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return hurt;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTick = 10;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public int getAttackAnimationTick() {
        return attackAnimationTick;
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(@NonNull BlockPos pos, @NonNull BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }
}
