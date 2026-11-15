package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.recipe.ConverterRecipe;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RiftConverterBlockEntity extends SyncableBlockEntity {

    private static final int LINK_CHECK_INTERVAL = 40;
    private static final int SYNC_INTERVAL = 4;

    private @Nullable GlobalPos link;

    private @Nullable BlockState sourceState;
    private @Nullable BlockState resultState;
    private int progress = 0;
    private int maxProgress = 1;
    private int energyPerTick = 0;

    private final SimpleRiftEnergyHandler riftEnergyHandler;

    public RiftConverterBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_CONVERTER_BLOCK_ENTITY.get(), pos, state);
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(RiftsStartupConfig.CONVERTER_ENERGY_CAPACITY.get()) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
    }

    public SimpleRiftEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    public @Nullable GlobalPos getLink() {
        return link;
    }

    public void setLink(@Nullable GlobalPos link) {
        this.link = link;
        setChanged();
    }

    public boolean isConverting() {
        return sourceState != null && resultState != null;
    }

    public @Nullable BlockState getSourceState() {
        return sourceState;
    }

    public @Nullable BlockState getResultState() {
        return resultState;
    }

    public float getProgressFraction() {
        return maxProgress <= 0 ? 0 : Math.min(1.0F, (float) progress / maxProgress);
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (link != null && serverLevel.getGameTime() % LINK_CHECK_INTERVAL == 0) {
            validateLink(serverLevel);
        }

        boolean active = link != null && getBlockState().getValue(SyncableBlock.RUNNING);
        BlockPos topPos = worldPosition.above();

        if (!isConverting()) {
            if (active) {
                tryStart(serverLevel, topPos);
            }
            return;
        }

        if (!active || riftEnergyHandler.getAmountAsInt() < energyPerTick) return;

        if (progress < maxProgress) {
            try (Transaction tx = Transaction.openRoot()) {
                riftEnergyHandler.extract(energyPerTick, tx);
                tx.commit();
            }
            progress++;

            if (serverLevel.getGameTime() % 4 == 0) {
                RiftParticleEffects.absorb(serverLevel, topPos.getX() + 0.5, topPos.getY() + 0.5, topPos.getZ() + 0.5, 2, 0.8, 1.4, 0.6);
            }
            if (progress % SYNC_INTERVAL == 0) {
                sync();
            }
        }

        if (progress >= maxProgress && serverLevel.getBlockState(topPos).isAir()) {
            serverLevel.setBlockAndUpdate(topPos, resultState);
            RiftParticleEffects.burst(serverLevel, topPos.getX() + 0.5, topPos.getY() + 0.5, topPos.getZ() + 0.5, 30, 0.5);
            clearConversion();
            sync();
        }
        setChanged();
    }

    private void tryStart(ServerLevel serverLevel, BlockPos topPos) {
        BlockState top = serverLevel.getBlockState(topPos);
        if (top.isAir() || serverLevel.getBlockEntity(topPos) != null || top.getDestroySpeed(serverLevel, topPos) < 0) return;
        if (link == null) return;

        for (RecipeHolder<ConverterRecipe> holder : serverLevel.getServer().getRecipeManager().recipeMap().byType(RiftsRecipeTypes.CONVERTER_TYPE.get())) {
            ConverterRecipe recipe = holder.value();
            if (!recipe.matchesBlock(top) || !recipe.allowsDimensions(serverLevel.dimension(), link.dimension())) continue;

            BlockState result = recipe.output().state();
            if (result.isAir() || riftEnergyHandler.getAmountAsInt() < recipe.riftEnergyPerTick()) continue;

            sourceState = top;
            resultState = result;
            progress = 0;
            maxProgress = Math.max(1, recipe.processingTime());
            energyPerTick = recipe.riftEnergyPerTick();
            serverLevel.setBlockAndUpdate(topPos, Blocks.AIR.defaultBlockState());
            sync();
            setChanged();
            return;
        }
    }

    private void clearConversion() {
        sourceState = null;
        resultState = null;
        progress = 0;
        maxProgress = 1;
        energyPerTick = 0;
    }

    private void validateLink(ServerLevel serverLevel) {
        GlobalPos target = link;
        if (target == null) return;

        ServerLevel partnerLevel = serverLevel.getServer().getLevel(target.dimension());
        boolean valid = partnerLevel != null && !partnerLevel.dimension().equals(serverLevel.dimension());
        if (valid && partnerLevel.hasChunkAt(target.pos())) {
            valid = partnerLevel.getBlockEntity(target.pos()) instanceof RiftConverterBlockEntity partner
                    && partner.link != null
                    && partner.link.dimension().equals(serverLevel.dimension())
                    && partner.link.pos().equals(worldPosition);
        }
        if (!valid) {
            setLink(null);
        }
    }

    public void unlink(ServerLevel serverLevel) {
        GlobalPos target = link;
        setLink(null);
        if (target == null) return;

        ServerLevel partnerLevel = serverLevel.getServer().getLevel(target.dimension());
        if (partnerLevel != null && partnerLevel.hasChunkAt(target.pos())
                && partnerLevel.getBlockEntity(target.pos()) instanceof RiftConverterBlockEntity partner
                && partner.link != null && partner.link.pos().equals(worldPosition)) {
            partner.setLink(null);
        }
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (level != null && !level.isClientSide() && sourceState != null) {
            ItemStack stack = new ItemStack(sourceState.getBlock().asItem());
            if (stack.isEmpty() && !sourceState.getFluidState().isEmpty()) {
                stack = new ItemStack(sourceState.getFluidState().getType().getBucket());
            }
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack));
            }
        }
        clearConversion();
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        output.storeNullable("link", GlobalPos.CODEC, link);
        output.storeNullable("sourceState", BlockState.CODEC, sourceState);
        output.storeNullable("resultState", BlockState.CODEC, resultState);
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putInt("energyPerTick", energyPerTick);
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        link = input.read("link", GlobalPos.CODEC).orElse(null);
        sourceState = input.read("sourceState", BlockState.CODEC).orElse(null);
        resultState = input.read("resultState", BlockState.CODEC).orElse(null);
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", 1);
        energyPerTick = input.getIntOr("energyPerTick", 0);
        riftEnergyHandler.deserialize(input.childOrEmpty("riftEnergyHandler"));
        super.loadAdditional(input);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        int energy = riftEnergyHandler.getAmountAsInt();
        if (energy > 0) {
            builder.set(RiftsDataComponents.RIFT_ENERGY.get(), energy);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        Integer energy = components.get(RiftsDataComponents.RIFT_ENERGY.get());
        if (energy != null) {
            riftEnergyHandler.set(Math.min(energy, riftEnergyHandler.getCapacityAsInt()));
        }
    }
}
