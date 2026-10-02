package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.InfuserEnergyHandler;
import com.benbenlaw.rifts.block.custom.RiftInfuserBlock;
import com.benbenlaw.rifts.recipe.InfuserRecipe;
import com.benbenlaw.rifts.recipe.InfuserRecipeInput;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import com.benbenlaw.rifts.screen.infuser.RiftInfuserMenu;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RiftInfuserBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 5, (slot, stack) -> slot <= 3, (slot) -> slot == 4){
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            progress = 0;
            updateCachedRecipe();
        }
    };
    private final InfuserEnergyHandler riftEnergyHandler = new InfuserEnergyHandler(1000000, 1000000, this);

    public InfuserEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    private RecipeHolder<InfuserRecipe> cachedRecipe;

    public RiftInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_INFUSER_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> RiftInfuserBlockEntity.this.progress;
                    case 1 -> RiftInfuserBlockEntity.this.maxProgress;
                    case 2 -> RiftInfuserBlockEntity.this.riftEnergyHandler.getAmountAsInt();
                    case 3 -> RiftInfuserBlockEntity.this.riftEnergyHandler.getCapacityAsInt();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> RiftInfuserBlockEntity.this.progress = value;
                    case 1 -> RiftInfuserBlockEntity.this.maxProgress = value;
                    case 2 -> riftEnergyHandler.getAmountAsInt();
                    case 3 -> riftEnergyHandler.getCapacityAsInt();
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public void tick() {
        if (level.isClientSide()) return;

        boolean running = level.getBlockState(worldPosition).getValue(RiftInfuserBlock.RUNNING);
        if (!running) {
            progress = 0;
            sync();
            return;
        }

        List<ItemStack> inputs = new ArrayList<>();
        inputs.add(inventory.getResource(0).toStack());
        inputs.add(inventory.getResource(1).toStack());
        inputs.add(inventory.getResource(2).toStack());
        inputs.add(inventory.getResource(3).toStack());

        if (inputs.isEmpty()) {
            progress = 0;
            sync();
            return;
        }

        boolean canCraft = cachedRecipe != null && hasEnoughRiftEnergy(cachedRecipe.value()) && canInsertOutput(cachedRecipe.value().output().create());

        if (canCraft) {
            progress++;
            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 3 == 0) {
                RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 0.9, worldPosition.getZ() + 0.5, 2, 0.7, 1.3, 0.5);
            }
            if (consumeRiftEnergy(cachedRecipe.value())) {
                if (progress >= maxProgress) {
                    craftItem();
                    sync();
                    progress = 0;
                }
            }
        }
    }

    private void updateCachedRecipe() {
        if (level != null && level.getServer() != null) {
            cachedRecipe = level.getServer().getRecipeManager().getRecipeFor(RiftsRecipeTypes.INFUSER_TYPE.get(),
                    new InfuserRecipeInput(inventory, riftEnergyHandler), level
            ).orElse(null);
        }
    }

    private boolean canInsertOutput(ItemStack output) {
        ItemStack outputSlot = ItemUtil.getStack(inventory, 4);
        if (outputSlot.isEmpty()) {
            return true;
        } else if (!ItemStack.isSameItemSameComponents(outputSlot, output)) {
            return false;
        } else {
            int result = outputSlot.getCount() + output.getCount();
            return result <= output.getMaxStackSize();
        }
    }

    private boolean consumeRiftEnergy(InfuserRecipe recipe) {
        try (Transaction tx = Transaction.openRoot()) {
            riftEnergyHandler.extract(recipe.riftEnergyPerTick(), tx);
            tx.commit();
            return true;
        }
    }

    private boolean hasEnoughRiftEnergy(InfuserRecipe recipe) {
        return riftEnergyHandler.getAmountAsInt() >= recipe.riftEnergyPerTick() * 200;
    }

    private void craftItem() {
        if (cachedRecipe == null) return;

        var recipe = cachedRecipe.value();
        int[] consumption = recipe.consumption(new InfuserRecipeInput(inventory, riftEnergyHandler));
        if (consumption == null) return;

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {

                for (int slot = 0; slot < consumption.length; slot++) {
                    ItemResource resource = inventory.getResource(slot);
                    if (resource.isEmpty() || consumption[slot] <= 0) continue;

                    inventory.extract(slot, resource, consumption[slot], tx);
                }

                inventory.insert(4, ItemResource.of(recipe.output()), recipe.output().create().getCount(), tx);

                tx.commit();
            }
        });
    }

    public SyncableItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftInfuserMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.rifts.rift_infuser");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        inventory.serialize(output.child("inventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        inventory.deserialize(input.childOrEmpty("inventory"));
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);
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
