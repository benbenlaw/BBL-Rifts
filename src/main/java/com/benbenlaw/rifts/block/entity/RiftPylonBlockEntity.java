package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.rifts.block.EpochopolisBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.block.capability.RiftEnergyHandler;
import com.benbenlaw.rifts.block.custom.RiftPylonBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.world.RiftEnergyData;
import com.benbenlaw.rifts.particle.RiftsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.benbenlaw.rifts.screen.pylon.RiftPylonMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RiftPylonBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private static final int DRAW_INTERVAL = 20;

    private static final int PARTICLE_INTERVAL = 3;

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private int absorbTicks = 0;
    private final ContainerData data;

    public RiftPylonBlockEntity(BlockPos pos, BlockState state) {
        super(EpochopolisBlockEntities.RIFT_PYLON_BLOCK_ENTITY.get(), pos, state);
        int capacity = state.getBlock() instanceof RiftPylonBlock pylon ? pylon.getCapacity() : 0;
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(capacity) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
        this.data = new RiftEnergyContainerData(riftEnergyHandler);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftPylonMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public RiftEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (absorbTicks > 0) {
            absorbTicks--;
            if (absorbTicks % PARTICLE_INTERVAL == 0) {
                spawnAbsorbParticle(serverLevel);
            }
        }

        if (level.getGameTime() % DRAW_INTERVAL != 0) return;
        if (!(getBlockState().getBlock() instanceof RiftPylonBlock pylon)) return;

        int space = riftEnergyHandler.getCapacityAsInt() - riftEnergyHandler.getAmountAsInt();
        int wanted = Math.min(space, pylon.getDrawPerSecond());
        if (wanted <= 0) return;

        int drawn = RiftEnergyData.get(serverLevel).draw(serverLevel, worldPosition, wanted);
        if (drawn > 0) {
            try (Transaction tx = Transaction.openRoot()) {
                riftEnergyHandler.insert(drawn, tx);
                tx.commit();
            }
            absorbTicks = DRAW_INTERVAL;
        }
    }

    private void spawnAbsorbParticle(ServerLevel serverLevel) {
        RandomSource random = serverLevel.getRandom();
        double theta = random.nextDouble() * Math.PI * 2;
        double distance = 1.2 + random.nextDouble() * 0.8;
        double dx = Math.cos(theta) * distance;
        double dz = Math.sin(theta) * distance;
        double dy = (random.nextDouble() - 0.3) * 1.5;

        serverLevel.sendParticles(RiftsParticles.RIFT_ABSORB.get(),
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.6, worldPosition.getZ() + 0.5,
                0, dx, dy, dz, 1.0);
    }

    public static void feedFromAdjacentPylons(Level level, BlockPos centre, RiftEnergyHandler target) {
        for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-1, -1, -1), centre.offset(1, 1, 1))) {
            if (pos.equals(centre)) continue;
            int space = target.getCapacityAsInt() - target.getAmountAsInt();
            if (space <= 0) return;

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RiftPylonBlockEntity pylon) {
                try (Transaction tx = Transaction.openRoot()) {
                    int extracted = pylon.riftEnergyHandler.extract(space, tx);
                    if (extracted > 0) {
                        int inserted = target.insert(extracted, tx);
                        if (inserted == extracted) {
                            tx.commit();
                        }
                    }
                }
            }
        }
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
