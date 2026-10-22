package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.block.custom.RiftQuarryBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.recipe.QuarryRecipe;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import com.benbenlaw.rifts.screen.quarry.RiftQuarryMenu;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RiftQuarryBlockEntity extends SyncableBlockEntity implements MenuProvider {

    public static final int FOCUS_SLOT = 0;
    public static final int OUTPUT_SLOTS = 6;
    private static final int FIRST_OUTPUT_SLOT = 1;

    private int progress = 0;
    private int maxProgress = 200;
    private boolean recipeDirty = true;
    private RecipeHolder<QuarryRecipe> cachedRecipe;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 1 + OUTPUT_SLOTS, (slot, stack) -> slot == FOCUS_SLOT, slot -> slot != FOCUS_SLOT) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            if (index == FOCUS_SLOT && !ItemStack.isSameItemSameComponents(previousContents, ItemUtil.getStack(this, FOCUS_SLOT))) {
                progress = 0;
            }
            recipeDirty = true;
        }
    };

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData energyData;
    private final ContainerData data;

    public RiftQuarryBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_QUARRY_BLOCK_ENTITY.get(), pos, state);
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(RiftsStartupConfig.QUARRY_ENERGY_CAPACITY.get()) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
        this.energyData = new RiftEnergyContainerData(riftEnergyHandler);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> energyData.get(index - 2);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    public SyncableItemHandler getItemHandler() {
        return inventory;
    }

    public SimpleRiftEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (recipeDirty) {
            updateCachedRecipe(serverLevel);
        }

        boolean powered = getBlockState().getValue(SyncableBlock.RUNNING);
        boolean working = false;

        if (cachedRecipe == null) {
            progress = 0;
        } else if (powered) {
            QuarryRecipe recipe = cachedRecipe.value();
            maxProgress = Math.max(1, recipe.processingTime());

            if (progress < maxProgress && riftEnergyHandler.getAmountAsInt() >= recipe.riftEnergyPerTick()) {
                consumeEnergy(recipe.riftEnergyPerTick());
                progress++;
                working = true;

                if (level.getGameTime() % 4 == 0) {
                    RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.6, 1.1, 0.4);
                }
            }

            // A finished cycle waits here while the outputs are full instead of losing the roll
            if (progress >= maxProgress && produce(serverLevel, recipe)) {
                progress = 0;
            }
        }

        BlockState state = getBlockState();
        if (state.getValue(RiftQuarryBlock.LIT) != working) {
            level.setBlock(worldPosition, state.setValue(RiftQuarryBlock.LIT, working), Block.UPDATE_ALL);
        }
        setChanged();
    }

    private void updateCachedRecipe(ServerLevel serverLevel) {
        recipeDirty = false;
        ItemStack focus = ItemUtil.getStack(inventory, FOCUS_SLOT);
        cachedRecipe = focus.isEmpty() ? null : serverLevel.getServer().getRecipeManager()
                .getRecipeFor(RiftsRecipeTypes.QUARRY_TYPE.get(), new SingleRecipeInput(focus), serverLevel)
                .orElse(null);
    }

    // Rolls one result from the weighted list and stores it; false when there is no room (or nothing valid to roll)
    private boolean produce(ServerLevel serverLevel, QuarryRecipe recipe) {
        ItemStack result = recipe.roll(serverLevel.getRandom());
        if (result.isEmpty()) return true;
        if (!canInsert(result)) return false;

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {
                insertOutput(result, tx);
                tx.commit();
            }
        });
        return true;
    }

    private boolean canInsert(ItemStack stack) {
        int remaining = stack.getCount();
        for (int i = 0; i < OUTPUT_SLOTS && remaining > 0; i++) {
            ItemStack slot = ItemUtil.getStack(inventory, FIRST_OUTPUT_SLOT + i);
            if (slot.isEmpty()) {
                remaining -= stack.getMaxStackSize();
            } else if (ItemStack.isSameItemSameComponents(slot, stack)) {
                remaining -= slot.getMaxStackSize() - slot.getCount();
            }
        }
        return remaining <= 0;
    }

    private void insertOutput(ItemStack stack, Transaction tx) {
        ItemResource resource = ItemResource.of(stack);
        int remaining = stack.getCount();

        for (int i = 0; i < OUTPUT_SLOTS && remaining > 0; i++) {
            int slot = FIRST_OUTPUT_SLOT + i;
            if (!inventory.getResource(slot).isEmpty() && inventory.getResource(slot).equals(resource)) {
                remaining -= inventory.insert(slot, resource, remaining, tx);
            }
        }
        for (int i = 0; i < OUTPUT_SLOTS && remaining > 0; i++) {
            int slot = FIRST_OUTPUT_SLOT + i;
            if (inventory.getResource(slot).isEmpty()) {
                remaining -= inventory.insert(slot, resource, remaining, tx);
            }
        }
    }

    private void consumeEnergy(int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            riftEnergyHandler.extract(amount, tx);
            tx.commit();
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftQuarryMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        inventory.serialize(output.child("inventory"));
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        inventory.deserialize(input.childOrEmpty("inventory"));
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", 200);
        riftEnergyHandler.deserialize(input.childOrEmpty("riftEnergyHandler"));
        recipeDirty = true;
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
