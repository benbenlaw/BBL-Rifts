package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.item.energy.RiftEnergyItem;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.screen.charger.RiftChargerMenu;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RiftChargerBlockEntity extends SyncableBlockEntity implements MenuProvider {

    public static final int SLOTS = 4;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, SLOTS,
            (slot, stack) -> stack.getItem() instanceof RiftEnergyItem, slot -> true);

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData data;

    public RiftChargerBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_CHARGER_BLOCK_ENTITY.get(), pos, state);
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(RiftsStartupConfig.CHARGER_ENERGY_CAPACITY.get()) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
        this.data = new RiftEnergyContainerData(riftEnergyHandler);
    }

    public SyncableItemHandler getItemHandler() {
        return inventory;
    }

    public SimpleRiftEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!getBlockState().getValue(SyncableBlock.RUNNING)) return;

        int rate = RiftsStartupConfig.CHARGER_RATE_PER_TICK.get();
        boolean charging = false;

        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack stack = ItemUtil.getStack(inventory, slot);
            if (!(stack.getItem() instanceof RiftEnergyItem item)) continue;

            int energy = RiftEnergyItem.getEnergy(stack);
            int transfer = Math.min(rate, Math.min(item.getMaxEnergy() - energy, riftEnergyHandler.getAmountAsInt()));
            if (transfer <= 0) continue;

            try (Transaction tx = Transaction.openRoot()) {
                riftEnergyHandler.extract(transfer, tx);
                tx.commit();
            }

            RiftEnergyItem.setEnergy(stack, energy + transfer);
            int finalSlot = slot;
            inventory.runInternal(() -> inventory.set(finalSlot, ItemResource.of(stack), stack.getCount()));
            charging = true;
        }

        if (charging && level.getGameTime() % 4 == 0) {
            RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.6, 1.1, 0.4);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftChargerMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        inventory.serialize(output.child("inventory"));
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        inventory.deserialize(input.childOrEmpty("inventory"));
        riftEnergyHandler.deserialize(input.childOrEmpty("riftEnergyHandler"));
        super.loadAdditional(input);
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
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
