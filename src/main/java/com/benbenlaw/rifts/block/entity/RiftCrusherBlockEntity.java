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
import com.benbenlaw.rifts.screen.MachineLayouts;
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

    private final int lines;
    private final int outputSlots;
    private final int[] progress;
    private final int[] maxProgress;
    private final RecipeHolder<CrusherRecipe>[] cachedRecipes;
    private boolean recipeDirty = true;

    private final SyncableItemHandler inventory;
    private final SimpleRiftEnergyHandler riftEnergyHandler;
    private final ContainerData energyData;
    private final ContainerData data;

    @SuppressWarnings("unchecked")
    public RiftCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_CRUSHER_BLOCK_ENTITY.get(), pos, state);
        this.lines = ((RiftCrusherBlock) state.getBlock()).getLines();
        this.outputSlots = MachineLayouts.crusherOutputs(lines);
        this.progress = new int[lines];
        this.maxProgress = new int[lines];
        this.cachedRecipes = new RecipeHolder[lines];
        for (int line = 0; line < lines; line++) {
            maxProgress[line] = 100;
        }

        this.inventory = new SyncableItemHandler(this, lines + outputSlots, (slot, stack) -> slot < lines, slot -> slot >= lines) {
            @Override
            protected void onContentsChanged(int index, ItemStack previousContents) {
                super.onContentsChanged(index, previousContents);
                if (index < lines && !ItemStack.isSameItemSameComponents(previousContents, ItemUtil.getStack(this, index))) {
                    progress[index] = 0;
                }
                recipeDirty = true;
            }
        };

        long capacity = (long) RiftsStartupConfig.CRUSHER_ENERGY_CAPACITY.get() * lines;
        this.riftEnergyHandler = new SimpleRiftEnergyHandler((int) Math.min(Integer.MAX_VALUE, capacity)) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
        this.energyData = new RiftEnergyContainerData(riftEnergyHandler);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (index < lines) return progress[index];
                if (index < lines * 2) return maxProgress[index - lines];
                return energyData.get(index - lines * 2);
            }

            @Override
            public void set(int index, int value) {
                if (index < lines) {
                    progress[index] = value;
                } else if (index < lines * 2) {
                    maxProgress[index - lines] = value;
                }
            }

            @Override
            public int getCount() {
                return lines * 2 + 4;
            }
        };
    }

    public int getLines() {
        return lines;
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
        boolean crushing = false;

        for (int line = 0; line < lines; line++) {
            CrusherRecipe recipe = cachedRecipes[line] == null ? null : cachedRecipes[line].value();
            if (recipe == null) {
                progress[line] = 0;
                continue;
            }
            if (!powered) continue;

            if (canInsertOutputs(recipe) && riftEnergyHandler.getAmountAsInt() >= recipe.riftEnergyPerTick()) {
                maxProgress[line] = Math.max(1, recipe.processingTime());
                consumeEnergy(recipe.riftEnergyPerTick());
                progress[line]++;
                crushing = true;

                if (progress[line] >= maxProgress[line]) {
                    craft(serverLevel, line, recipe);
                    progress[line] = 0;
                }
            }
        }

        if (crushing && level.getGameTime() % 4 == 0) {
            RiftParticleEffects.absorb(serverLevel, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.6, 1.1, 0.4);
        }

        BlockState state = getBlockState();
        if (state.getValue(RiftCrusherBlock.LIT) != crushing) {
            level.setBlock(worldPosition, state.setValue(RiftCrusherBlock.LIT, crushing), Block.UPDATE_ALL);
        }
        setChanged();
    }

    private void updateCachedRecipes(ServerLevel serverLevel) {
        recipeDirty = false;
        for (int line = 0; line < lines; line++) {
            ItemStack input = ItemUtil.getStack(inventory, line);
            cachedRecipes[line] = input.isEmpty() ? null : serverLevel.getServer().getRecipeManager()
                    .getRecipeFor(RiftsRecipeTypes.CRUSHER_TYPE.get(), new SingleRecipeInput(input), serverLevel)
                    .orElse(null);
        }
    }

    private boolean canInsertOutputs(CrusherRecipe recipe) {
        ItemStack[] simulated = new ItemStack[outputSlots];
        for (int i = 0; i < outputSlots; i++) {
            simulated[i] = ItemUtil.getStack(inventory, lines + i).copy();
        }

        if (!simulatePlace(simulated, CrusherRecipe.resolve(recipe.output()))) return false;
        return recipe.bonus().map(bonus -> {
            ItemStack bonusStack = CrusherRecipe.resolve(bonus.item());
            return bonusStack.isEmpty() || simulatePlace(simulated, bonusStack);
        }).orElse(true);
    }

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

        for (int i = 0; i < outputSlots && remaining > 0; i++) {
            int slot = lines + i;
            if (!inventory.getResource(slot).isEmpty() && inventory.getResource(slot).equals(resource)) {
                remaining -= inventory.insert(slot, resource, remaining, tx);
            }
        }
        for (int i = 0; i < outputSlots && remaining > 0; i++) {
            int slot = lines + i;
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

    private void craft(ServerLevel serverLevel, int line, CrusherRecipe recipe) {
        ItemStack output = CrusherRecipe.resolve(recipe.output());
        ItemStack bonusStack = recipe.bonus()
                .filter(bonus -> serverLevel.getRandom().nextFloat() < bonus.chance())
                .map(bonus -> CrusherRecipe.resolve(bonus.item()))
                .orElse(ItemStack.EMPTY);

        inventory.runInternal(() -> {
            try (Transaction tx = Transaction.openRoot()) {
                inventory.extract(line, inventory.getResource(line), recipe.ingredient().count(), tx);
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
        return new RiftCrusherMenu(MachineLayouts.tierOf(lines), container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        inventory.serialize(output.child("inventory"));
        for (int line = 0; line < lines; line++) {
            output.putInt("progress" + line, progress[line]);
            output.putInt("maxProgress" + line, maxProgress[line]);
        }
        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        inventory.deserialize(input.childOrEmpty("inventory"));
        for (int line = 0; line < lines; line++) {
            progress[line] = input.getIntOr("progress" + line, 0);
            maxProgress[line] = input.getIntOr("maxProgress" + line, 100);
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
