package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.screen.surge.RiftSurgeCoreMenu;
import com.benbenlaw.rifts.surge.RiftSurgeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RiftSurgeCoreBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData data;

    public RiftSurgeCoreBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_SURGE_CORE_BLOCK_ENTITY.get(), pos, state);
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(RiftsStartupConfig.SURGE_CORE_ENERGY_CAPACITY.get()) {
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

        int energy = riftEnergyHandler.getAmountAsInt();
        int capacity = riftEnergyHandler.getCapacityAsInt();

        if (energy >= capacity) {
            overload(serverLevel);
            return;
        }

        if (energy > 0 && serverLevel.getGameTime() % 10 == 0) {
            int count = 1 + (int) (4.0 * energy / capacity);
            RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 0.8, worldPosition.getZ() + 0.5, count, 0.8, 1.6, 0.6);
        }
    }

    private void overload(ServerLevel serverLevel) {
        BlockPos pos = worldPosition;
        serverLevel.removeBlock(pos, false);
        serverLevel.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                RiftsStartupConfig.SURGE_EXPLOSION_POWER.get().floatValue(), Level.ExplosionInteraction.NONE);
        RiftParticleEffects.burst(serverLevel, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 80, 1.2);
        RiftSurgeManager.start(serverLevel, pos);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftSurgeCoreMenu(container, inventory, this.worldPosition, data);
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
