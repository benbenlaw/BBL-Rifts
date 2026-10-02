package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.accelerator.TickAcceleration;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.block.custom.RiftTickAcceleratorBlock;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.screen.pylon.RiftPylonMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RiftTickAcceleratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private static final int BUFFER_TICKS = 200;

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData data;

    public RiftTickAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_TICK_ACCELERATOR_BLOCK_ENTITY.get(), pos, state);
        int energyPerTick = state.getBlock() instanceof RiftTickAcceleratorBlock block ? block.getEnergyPerTick() : 0;
        int capacity = (int) Math.min(Integer.MAX_VALUE, (long) energyPerTick * BUFFER_TICKS);
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(capacity) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
        this.data = new RiftEnergyContainerData(riftEnergyHandler);
    }

    public SimpleRiftEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(getBlockState().getBlock() instanceof RiftTickAcceleratorBlock block)) return;
        if (!getBlockState().getValue(SyncableBlock.RUNNING)) return;
        if (riftEnergyHandler.getAmountAsInt() < block.getEnergyPerTick()) return;

        List<BlockPos> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos target = worldPosition.relative(direction);
            if (TickAcceleration.isTarget(serverLevel, target)) {
                targets.add(target);
            }
        }
        if (targets.isEmpty()) return;

        try (Transaction tx = Transaction.openRoot()) {
            riftEnergyHandler.extract(block.getEnergyPerTick(), tx);
            tx.commit();
        }

        for (BlockPos target : targets) {
            int granted = TickAcceleration.reserve(serverLevel, target, block.getExtraTicks());
            if (granted > 0) {
                TickAcceleration.accelerate(serverLevel, target, granted);
            }
        }

        if (level.getGameTime() % 3 == 0) {
            for (BlockPos target : targets) {
                RiftParticleEffects.absorb(serverLevel, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 2, 0.7, 1.3, 0.5);
            }
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftPylonMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
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
