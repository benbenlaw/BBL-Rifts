package com.benbenlaw.rifts.block.entity;

import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.RiftsBlockEntities;
import com.benbenlaw.rifts.block.capability.InfuserEnergyHandler;
import com.benbenlaw.rifts.recipe.RiftRecipe;
import com.benbenlaw.rifts.screen.generator.RiftGeneratorMenu;
import net.minecraft.core.BlockPos;
import com.benbenlaw.rifts.particle.RiftParticleEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RiftGeneratorBlockEntity extends SyncableBlockEntity implements MenuProvider {

    public enum RiftState { IDLE, CHARGING, ACTIVE, CLOSING }

    private static final int CHARGE_ITEM_SLOT = 0;
    private static final double STAND_DETECT_HEIGHT = 0.6;

    private final ContainerData data;
    private int maxProgress = 200;
    private int progress = 0;
    private RiftState state = RiftState.IDLE;
    private int riftTimeRemaining = 0;
    private FakePlayer fakePlayer;

    private float[] activeBase = {0.62f, 0.22f, 1f};
    private float[] activeBright = {0.78f, 0.55f, 1f};
    private float[] activeBoltCore = {0.92f, 0.85f, 1f};
    private float[] closingBase = {1f, 0.25f, 0.2f};
    private float[] closingBright = {1f, 0.55f, 0.35f};
    private float[] closingBoltCore = {1f, 0.9f, 0.75f};

    private RiftRecipe activeRecipe;

    private final Set<UUID> displacedPlayers = new HashSet<>();

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 4,
            (slot, stack) -> slot != CHARGE_ITEM_SLOT || findRecipeFor(stack) != null,
            (slot) -> false);

    private final InfuserEnergyHandler riftEnergyHandler = new InfuserEnergyHandler(1000000, 1000000, this);

    public InfuserEnergyHandler getRiftEnergyHandler() {
        return riftEnergyHandler;
    }

    public RiftGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(RiftsBlockEntities.RIFT_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> RiftGeneratorBlockEntity.this.progress;
                    case 1 -> RiftGeneratorBlockEntity.this.maxProgress;
                    case 2 -> RiftGeneratorBlockEntity.this.riftTimeRemaining;
                    case 3 -> RiftGeneratorBlockEntity.this.state.ordinal();
                    case 4 -> RiftGeneratorBlockEntity.this.riftEnergyHandler.getAmountAsInt();
                    case 5 -> RiftGeneratorBlockEntity.this.riftEnergyHandler.getCapacityAsInt();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> RiftGeneratorBlockEntity.this.progress = value;
                    case 1 -> RiftGeneratorBlockEntity.this.maxProgress = value;
                    case 2 -> RiftGeneratorBlockEntity.this.riftTimeRemaining = value;
                    case 3 -> RiftGeneratorBlockEntity.this.state = RiftState.values()[value];
                    case 4 -> riftEnergyHandler.getAmountAsInt();
                    case 5 -> riftEnergyHandler.getCapacityAsInt();
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    public void tick() {
        assert level != null;
        if (level.isClientSide()) return;

        switch (state) {
            case IDLE -> tryStartCharging();
            case CHARGING -> {
                doCharging();
                spawnRiftParticles();
            }
            case ACTIVE -> {
                riftTimeRemaining--;
                pullNearbyPlayersIn();
                spawnRiftParticles();
                if (activeRecipe != null && riftTimeRemaining <= activeRecipe.closingWarningTime()) {
                    state = RiftState.CLOSING;
                }
            }
            case CLOSING -> {
                riftTimeRemaining--;
                pullNearbyPlayersIn();
                spawnRiftParticles();
                if (riftTimeRemaining <= 0) {
                    closeRift();
                }
            }
        }
        setChanged();
    }

    private void spawnChargingParticles(ServerLevel serverLevel, double cx, double cz) {
        double y = worldPosition.getY() + 1.05;
        float intensity = (float) progress / maxProgress;

        int count = 1 + Math.round(intensity * 3);
        RiftParticleEffects.absorb(serverLevel, cx, y, cz, count, 0.7, 1.3 + intensity * 0.5, 0.4);

        if (intensity > 0.8f) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, cx, y, cz, 1, 0.15, 0.05, 0.15, 0.005);
        }
    }

    private @Nullable RecipeHolder<RiftRecipe> findRecipeHolderFor(ItemStack stack) {
        if (level == null || level.getServer() == null) return null;
        for (RecipeHolder<RiftRecipe> holder : level.getServer().getRecipeManager().recipeMap().byType(RiftRecipe.TYPE)) {
            if (ItemStack.isSameItemSameComponents(holder.value().chargeItem().create(), stack)) {
                return holder;
            }
        }
        return null;
    }

    private @Nullable RiftRecipe findRecipeFor(ItemStack stack) {
        RecipeHolder<RiftRecipe> holder = findRecipeHolderFor(stack);
        return holder == null ? null : holder.value();
    }

    private void tryStartCharging() {
        ItemStack stack = ItemUtil.getStack(inventory, CHARGE_ITEM_SLOT);
        RiftRecipe recipe = findRecipeFor(stack);
        if (recipe != null) {
            activeRecipe = recipe;
            cacheRecipeColors(recipe);
            state = RiftState.CHARGING;
            progress = 0;
            maxProgress = recipe.startupTime();
        }
    }

    private void cacheRecipeColors(RiftRecipe recipe) {
        activeBase = recipe.activeColors().base().toArray();
        activeBright = recipe.activeColors().bright().toArray();
        activeBoltCore = recipe.activeColors().boltCore().toArray();
        closingBase = recipe.closingColors().base().toArray();
        closingBright = recipe.closingColors().bright().toArray();
        closingBoltCore = recipe.closingColors().boltCore().toArray();
        sync();
    }

    private void doCharging() {
        ItemStack stack = ItemUtil.getStack(inventory, CHARGE_ITEM_SLOT);
        if (activeRecipe == null || !ItemStack.isSameItemSameComponents(activeRecipe.chargeItem().create(), stack)) {
            state = RiftState.IDLE;
            progress = 0;
            activeRecipe = null;
            return;
        }
        progress++;
        if (consumeRiftEnergy(activeRecipe)) {
            if (progress >= maxProgress) {
                inventory.runInternal(() -> {
                    try (Transaction tx = Transaction.openRoot()) {
                        inventory.extract(CHARGE_ITEM_SLOT, inventory.getResource(CHARGE_ITEM_SLOT), 1, tx);
                        tx.commit();
                    }
                });
                openRift();
            }
        }
    }

    private void openRift() {
        if (activeRecipe == null) return;
        state = RiftState.ACTIVE;
        riftTimeRemaining = activeRecipe.openTime();
        progress = 0;
        sync();
        setChunkForced(true);
    }

    private boolean consumeRiftEnergy(RiftRecipe recipe) {
        try (Transaction tx = Transaction.openRoot()) {
            riftEnergyHandler.extract(recipe.riftEnergyPerTick(), tx);
            tx.commit();
            return true;
        }
    }

    private void closeRift() {
        assert level != null;
        state = RiftState.IDLE;
        riftTimeRemaining = 0;

        if (activeRecipe != null && level.getServer() != null) {
            ServerLevel toLevel = level.getServer().getLevel(activeRecipe.toDimension());
            if (toLevel != null) {
                for (UUID uuid : new HashSet<>(displacedPlayers)) {
                    ServerPlayer player = (ServerPlayer) toLevel.getPlayerByUUID(uuid);
                    if (player != null) {
                        returnFromOtherSide(player);
                    }
                }
            }
        }
        sync();
        displacedPlayers.clear();
        setChunkForced(false);
        activeRecipe = null;
    }

    private void pullNearbyPlayersIn() {
        assert level != null;
        if (level.getServer() == null || activeRecipe == null) return;

        ServerLevel toLevel = level.getServer().getLevel(activeRecipe.toDimension());
        if (toLevel == null) return;

        AABB standingBox = new AABB(
                worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 1 + STAND_DETECT_HEIGHT, worldPosition.getZ() + 1
        );

        for (Player player : level.getEntitiesOfClass(Player.class, standingBox)) {
            if (player instanceof ServerPlayer serverPlayer && !displacedPlayers.contains(player.getUUID())) {
                sendToOtherSide(serverPlayer, toLevel);
            }
        }
    }

    private BlockPos findSafeLanding(ServerLevel targetLevel, int x, int z) {
        int minY = targetLevel.getMinY() + 1;
        int maxY = targetLevel.getMaxY() - 2;
        boolean hasCeiling = targetLevel.dimensionType().hasCeiling();

        int startY = hasCeiling
                ? Math.min(maxY, 100)
                : targetLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        BlockPos found = scanColumn(targetLevel, x, z, startY, minY, -1);
        if (found != null) return found;

        if (hasCeiling) {
            found = spiralSearch(targetLevel, x, z, startY, minY);
            if (found != null) return found;
        } else {
            found = scanColumn(targetLevel, x, z, startY, maxY, 1);
            if (found != null) return found;
        }

        return buildEmergencyPlatform(targetLevel, x, startY, z);
    }

    private BlockPos spiralSearch(ServerLevel level, int centerX, int centerZ, int startY, int minY) {
        int maxRadius = 24;
        for (int radius = 4; radius <= maxRadius; radius += 4) {
            for (int dx = -radius; dx <= radius; dx += 4) {
                for (int dz = -radius; dz <= radius; dz += 4) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    BlockPos found = scanColumn(level, centerX + dx, centerZ + dz, startY, minY, -1);
                    if (found != null) return found;
                }
            }
        }
        return null;
    }

    private BlockPos buildEmergencyPlatform(ServerLevel level, int x, int y, int z) {
        BlockPos base = new BlockPos(x, y, z);
        level.setBlockAndUpdate(base.below(), net.minecraft.world.level.block.Blocks.NETHERRACK.defaultBlockState());
        level.setBlockAndUpdate(base, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(base.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        return base;
    }

    private BlockPos scanColumn(ServerLevel level, int x, int z, int startY, int limitY, int step) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        for (int y = startY; step > 0 ? y <= limitY : y >= limitY; y += step) {
            pos.setY(y);
            if (isSafeColumn(level, pos)) {
                return pos.immutable();
            }
        }
        return null;
    }

    private boolean isSafeColumn(ServerLevel level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());
        BlockState ground = level.getBlockState(pos.below());

        boolean feetClear = feet.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty()
                && feet.getFluidState().isEmpty();
        boolean headClear = head.getCollisionShape(level, pos.above(), CollisionContext.empty()).isEmpty()
                && head.getFluidState().isEmpty();
        boolean groundSolid = !ground.getCollisionShape(level, pos.below(), CollisionContext.empty()).isEmpty()
                && ground.getFluidState().isEmpty();

        return feetClear && headClear && groundSolid;
    }

    private void sendToOtherSide(ServerPlayer player, ServerLevel toLevel) {
        displacedPlayers.add(player.getUUID());
        BlockPos landing = findSafeLanding(toLevel, worldPosition.getX(), worldPosition.getZ());
        player.teleportTo(toLevel, landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5,
                Set.of(), player.getYRot(), player.getXRot(), true);
    }

    private void returnFromOtherSide(ServerPlayer player) {
        assert level instanceof ServerLevel;
        displacedPlayers.remove(player.getUUID());
        ServerLevel homeLevel = (ServerLevel) level;

        BlockPos onBlock = worldPosition.above();
        BlockPos landing = isSafeColumn(homeLevel, onBlock)
                ? onBlock
                : findSafeLanding(homeLevel, worldPosition.getX(), worldPosition.getZ());

        player.teleportTo(homeLevel, landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5,
                Set.of(), player.getYRot(), player.getXRot(), true);
    }

    public ItemStacksResourceHandler getItemHandler() {
        return inventory;
    }

    public RiftState getRiftState() {
        return state;
    }

    public int getRiftTimeRemaining() {
        return riftTimeRemaining;
    }

    public float[] getActiveBaseColor() {
        return activeBase;
    }

    public float[] getActiveBrightColor() {
        return activeBright;
    }

    public float[] getActiveBoltCore() {
        return activeBoltCore;
    }

    public float[] getClosingBaseColor() {
        return closingBase;
    }

    public float[] getClosingBrightColor() {
        return closingBright;
    }

    public float[] getClosingBoltCore() {
        return closingBoltCore;
    }

    private void spawnRiftParticles() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (level.getGameTime() % 3 != 0) return;

        double cx = worldPosition.getX() + 0.5;
        double lowerY = worldPosition.getY() + 1.2;
        double upperY = worldPosition.getY() + 3.5;
        double cz = worldPosition.getZ() + 0.5;

        if (state == RiftState.CHARGING) {
            spawnChargingParticles(serverLevel, cx, cz);
            return;
        }

        RiftParticleEffects.absorb(serverLevel, cx, lowerY, cz, 4, 1.0, 2.0, 0.8);
        serverLevel.sendParticles(ParticleTypes.END_ROD, cx, upperY, cz, 2, 0.35, 0.15, 0.35, 0.015);

        if (state == RiftState.CLOSING) {
            RiftParticleEffects.absorb(serverLevel, cx, lowerY, cz, 8, 1.0, 2.2, 1.0);
        }
    }

    private void setChunkForced(boolean forced) {
        if (level instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = ChunkPos.containing(worldPosition);
            serverLevel.setChunkForced(chunkPos.x(), chunkPos.z(), forced);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container, Inventory inventory, Player player) {
        return new RiftGeneratorMenu(container, inventory, this.worldPosition, data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.rifts.rift_generator");
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        inventory.serialize(output.child("inventory"));
        output.putInt("maxProgress", maxProgress);
        output.putInt("progress", progress);
        output.putInt("riftTimeRemaining", riftTimeRemaining);
        output.putInt("state", state.ordinal());

        putColor(output, "activeBase", activeBase);
        putColor(output, "activeBright", activeBright);
        putColor(output, "activeBoltCore", activeBoltCore);
        putColor(output, "closingBase", closingBase);
        putColor(output, "closingBright", closingBright);
        putColor(output, "closingBoltCore", closingBoltCore);

        riftEnergyHandler.serialize(output.child("riftEnergyHandler"));

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        inventory.deserialize(input.childOrEmpty("inventory"));
        maxProgress = input.getIntOr("maxProgress", 200);
        progress = input.getIntOr("progress", 0);
        riftTimeRemaining = input.getIntOr("riftTimeRemaining", 0);
        state = RiftState.values()[input.getIntOr("state", 0)];

        activeBase = getColorOr(input, "activeBase", activeBase);
        activeBright = getColorOr(input, "activeBright", activeBright);
        activeBoltCore = getColorOr(input, "activeBoltCore", activeBoltCore);
        closingBase = getColorOr(input, "closingBase", closingBase);
        closingBright = getColorOr(input, "closingBright", closingBright);
        closingBoltCore = getColorOr(input, "closingBoltCore", closingBoltCore);

        riftEnergyHandler.deserialize(input.childOrEmpty("riftEnergyHandler"));

        super.loadAdditional(input);
    }

    private void putColor(ValueOutput output, String key, float[] color) {
        output.putFloat(key + "R", color[0]);
        output.putFloat(key + "G", color[1]);
        output.putFloat(key + "B", color[2]);
    }

    private float[] getColorOr(ValueInput input, String key, float[] fallback) {
        return new float[]{
                input.getFloatOr(key + "R", fallback[0]),
                input.getFloatOr(key + "G", fallback[1]),
                input.getFloatOr(key + "B", fallback[2])
        };
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);

        if (activeRecipe != null && level != null && level.getServer() != null && !displacedPlayers.isEmpty()) {
            ServerLevel toLevel = level.getServer().getLevel(activeRecipe.toDimension());
            if (toLevel != null) {
                for (UUID uuid : new HashSet<>(displacedPlayers)) {
                    ServerPlayer player = (ServerPlayer) toLevel.getPlayerByUUID(uuid);
                    if (player != null) {
                        returnFromOtherSide(player);
                    }
                }
            }
        }
        displacedPlayers.clear();
        setChunkForced(false);
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
