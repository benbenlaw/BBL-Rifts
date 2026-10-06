package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.RiftEnergyContainerData;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import com.benbenlaw.rifts.block.custom.RiftCrusherBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import com.benbenlaw.rifts.recipe.CrusherRecipe;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import com.benbenlaw.rifts.screen.crusher.RiftCrusherMenu;
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

public class RiftCrusherBlockEntity extends SyncableBlockEntity implements MenuProvider {

    private static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOTS = 4;
    private static final int FIRST_OUTPUT_SLOT = 1;

    private int progress = 0;
    private int maxProgress = 100;
    private boolean recipeDirty = true;
    private RecipeHolder<CrusherRecipe> cachedRecipe;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 1 + OUTPUT_SLOTS, (slot, stack) -> slot == INPUT_SLOT, slot -> slot != INPUT_SLOT) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            if (index == INPUT_SLOT && !ItemStack.isSameItemSameComponents(previousContents, ItemUtil.getStack(this, INPUT_SLOT))) {
                progress = 0;
            }
            recipeDirty = true;
        }
    };

    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData energyData;
    private final ContainerData data;

    public RiftCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_CRUSHER_BLOCK_ENTITY.get(), pos, state);
        this.riftEnergyHandler = new SimpleRiftEnergyHandler(RiftsStartupConfig.CRUSHER_ENERGY_CAPACITY.get()) {
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
        boolean crushing = false;

        if (cachedRecipe == null) {
            progress = 0;
        } else if (powered) {
            CrusherRecipe recipe = cachedRecipe.value();

            if (canInsertOutputs(recipe) && riftEnergyHandler.getAmountAsInt() >= recipe.riftEnergyPerTick()) {
                maxProgress = Math.max(1, recipe.processingTime());
                consumeEnergy(recipe.riftEnergyPerTick());
                progress++;
                crushing = true;

                if (level.getGameTime() % 4 == 0) {
                    RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.6, 1.1, 0.4);
                }

                if (progress >= maxProgress) {
                    craft(serverLevel, recipe);
                    progress = 0;
                }
            }
        }

        BlockState state = getBlockState();
        if (state.getValue(RiftCrusherBlock.LIT) != crushing) {
            level.setBlock(worldPosition, state.setValue(RiftCrusherBlock.LIT, crushing), Block.UPDATE_ALL);
        }
        setChanged();
    }

    private void updateCachedRecipe(ServerLevel serverLevel) {
        recipeDirty = false;
        ItemStack input = ItemUtil.getStack(inventory, INPUT_SLOT);
        cachedRecipe = input.isEmpty() ? null : serverLevel.getServer().getRecipeManager()
                .getRecipeFor(RiftsRecipeTypes.CRUSHER_TYPE.get(), new SingleRecipeInput(input), serverLevel)
                .orElse(null);
    }

    private boolean canInsertOutputs(CrusherRecipe recipe) {
        ItemStack[] simulated = new ItemStack[OUTPUT_SLOTS];
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            simulated[i] = ItemUtil.getStack(inventory, FIRST_OUTPUT_SLOT + i).copy();
        }

        if (!simulatePlace(simulated, CrusherRecipe.resolve(recipe.output()))) return false;
        return recipe.bonus().map(bonus -> {
            ItemStack bonusStack = CrusherRecipe.resolve(bonus.item());
            return bonusStack.isEmpty() || simulatePlace(simulated, bonusStack);
        }).orElse(true);
    }

    // Outputs merge into a matching stack first, then take the first empty slot
    private static boolean simulatePlace(ItemStack[] slots, ItemStack stack) {
        if (stack.isEmpty()) return false;
        int remaining = stack.getCount();

        for (ItemStack slot : slots) {
            if (remaining <= 0) break;
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                int moved = Math.min(remaining, slot.getMaxStackSize() - slot.getCount());
                slot.grow(moved);
                remaining -= moved;
            }
        }

        for (int i = 0; i < slots.length && remaining > 0; i++) {
            if (slots[i].isEmpty()) {
                int moved = Math.min(remaining, stack.getMaxStackSize());
                slots[i] = stack.copyWithCount(moved);
                remaining -= moved;
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

    private void craft(ServerLevel serverLevel, CrusherRecipe recipe) {
        ItemStack output = CrusherRecipe.resolve(recipe.output());
        ItemStack bonusStack = recipe.bonus()
                .filter(bonus -> serverLevel.getRandom().nextFloat() < bonus.chance())
                .map(bonus -> CrusherRecipe.resolve(bonus.item()))
                .orElse(ItemStack.EMPTY);

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {
                inventory.extract(INPUT_SLOT, inventory.getResource(INPUT_SLOT), recipe.ingredient().count(), tx);
                insertOutput(output, tx);
                if (!bonusStack.isEmpty()) {
                    insertOutput(bonusStack, tx);
                }
                tx.commit();
            }
        });
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftCrusherMenu(container, inventory, this.worldPosition, data);
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
        maxProgress = input.getIntOr("maxProgress", 100);
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
