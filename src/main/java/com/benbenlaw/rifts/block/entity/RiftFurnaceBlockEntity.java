package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.block.custom.RiftFurnaceBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.screen.furnace.RiftFurnaceMenu;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RiftFurnaceBlockEntity extends SyncableBlockEntity implements MenuProvider {

    public static final int LINES = 6;

    private final int[] progress = new int[LINES];
    private final int[] maxProgress = new int[LINES];
    private final RecipeHolder<SmeltingRecipe>[] cachedRecipes = new RecipeHolder[LINES];
    private boolean recipeDirty = true;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, LINES * 2, (slot, stack) -> slot < LINES, slot -> slot >= LINES) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            if (index < LINES && !ItemStack.isSameItemSameComponents(previousContents, ItemUtil.getStack(this, index))) {
                progress[index] = 0;
            }
            recipeDirty = true;
        }
    };

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData energyData;
    private final ContainerData data;

    public RiftFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_FURNACE_BLOCK_ENTITY.get(), pos, state);
        for (int line = 0; line < LINES; line++) {
            maxProgress[line] = 200;
        }
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(RiftsStartupConfig.FURNACE_ENERGY_CAPACITY.get()) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
        this.energyData = new RiftEnergyContainerData(riftEnergyHandler);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (index < LINES) return progress[index];
                if (index < LINES * 2) return maxProgress[index - LINES];
                return energyData.get(index - LINES * 2);
            }

            @Override
            public void set(int index, int value) {
                if (index < LINES) {
                    progress[index] = value;
                } else if (index < LINES * 2) {
                    maxProgress[index - LINES] = value;
                }
            }

            @Override
            public int getCount() {
                return LINES * 2 + 4;
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
            updateCachedRecipes(serverLevel);
        }

        boolean powered = getBlockState().getValue(SyncableBlock.RUNNING);
        int energyPerTick = RiftsStartupConfig.FURNACE_ENERGY_PER_TICK.get();
        boolean anyCooking = false;

        for (int line = 0; line < LINES; line++) {
            RecipeHolder<SmeltingRecipe> recipe = cachedRecipes[line];
            if (recipe == null) {
                progress[line] = 0;
                continue;
            }
            if (!powered) continue;

            ItemStack result = recipe.value().assemble(new SingleRecipeInput(ItemUtil.getStack(inventory, line)));
            if (!canInsertOutput(line, result) || riftEnergyHandler.getAmountAsInt() < energyPerTick) continue;

            maxProgress[line] = Math.max(1, recipe.value().cookingTime() / RiftsStartupConfig.FURNACE_SPEED.get());
            consumeEnergy(energyPerTick);
            progress[line]++;
            anyCooking = true;

            if (progress[line] >= maxProgress[line]) {
                craft(line, result);
                progress[line] = 0;
            }
        }

        if (anyCooking && level.getGameTime() % 4 == 0) {
            RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.6, 1.1, 0.4);
        }

        BlockState state = getBlockState();
        if (state.getValue(RiftFurnaceBlock.LIT) != anyCooking) {
            level.setBlock(worldPosition, state.setValue(RiftFurnaceBlock.LIT, anyCooking), Block.UPDATE_ALL);
        }
        setChanged();
    }

    private void updateCachedRecipes(ServerLevel serverLevel) {
        recipeDirty = false;
        for (int line = 0; line < LINES; line++) {
            ItemStack input = ItemUtil.getStack(inventory, line);
            cachedRecipes[line] = input.isEmpty() ? null : serverLevel.getServer().getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), serverLevel)
                    .orElse(null);
        }
    }

    private boolean canInsertOutput(int line, ItemStack result) {
        if (result.isEmpty()) return false;
        ItemStack outputSlot = ItemUtil.getStack(inventory, LINES + line);
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= result.getMaxStackSize();
    }

    private void consumeEnergy(int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            riftEnergyHandler.extract(amount, tx);
            tx.commit();
        }
    }

    private void craft(int line, ItemStack result) {
        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {
                inventory.extract(line, inventory.getResource(line), 1, tx);
                inventory.insert(LINES + line, ItemResource.of(result), result.getCount(), tx);
                tx.commit();
            }
        });
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftFurnaceMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        inventory.serialize(output.child("inventory"));
        for (int line = 0; line < LINES; line++) {
            output.putInt("progress" + line, progress[line]);
            output.putInt("maxProgress" + line, maxProgress[line]);
        }
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        inventory.deserialize(input.childOrEmpty("inventory"));
        for (int line = 0; line < LINES; line++) {
            progress[line] = input.getIntOr("progress" + line, 0);
            maxProgress[line] = input.getIntOr("maxProgress" + line, 200);
        }
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
