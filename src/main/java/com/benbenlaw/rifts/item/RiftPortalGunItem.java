package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.entity.RiftPortal;
import com.benbenlaw.rifts.item.energy.RiftEnergyItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RiftPortalGunItem extends Item implements RiftEnergyItem {

    public static final int FIRST_PORTAL = 0;
    public static final int SECOND_PORTAL = 1;
    private static final int FIRE_COOLDOWN = 6;

    public RiftPortalGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxEnergy() {
        return RiftsStartupConfig.PORTAL_GUN_CAPACITY.get();
    }

    @Override
    public int getEnergyPerDamage() {
        return 0;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return RiftEnergyItem.isBarVisible(stack, this);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return RiftEnergyItem.getBarWidth(stack, this);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            fire(serverPlayer, player.getItemInHand(hand), FIRST_PORTAL);
        }
        return InteractionResult.SUCCESS;
    }

    public static DyeColor portalColor(ItemStack stack, int slot) {
        return slot == FIRST_PORTAL
                ? stack.getOrDefault(RiftsDataComponents.PORTAL_GUN_FIRST_COLOR.get(), DyeColor.BLUE)
                : stack.getOrDefault(RiftsDataComponents.PORTAL_GUN_SECOND_COLOR.get(), DyeColor.ORANGE);
    }

    public static void fire(ServerPlayer player, ItemStack stack, int slot) {
        if (player.getCooldowns().isOnCooldown(stack)) return;
        player.getCooldowns().addCooldown(stack, FIRE_COOLDOWN);

        ServerLevel level = player.level();
        int cost = RiftsStartupConfig.PORTAL_GUN_ENERGY_PER_SHOT.get();
        boolean free = player.hasInfiniteMaterials() || cost == 0;
        if (!free && RiftEnergyItem.getEnergy(stack) < cost) {
            player.sendOverlayMessage(Component.translatable("message.rifts.portal_gun_empty"));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.0F);
            return;
        }

        UUID id = stack.get(RiftsDataComponents.PORTAL_GUN_ID.get());
        if (id == null) {
            id = UUID.randomUUID();
            stack.set(RiftsDataComponents.PORTAL_GUN_ID.get(), id);
        }

        HitResult hit = player.pick(RiftsStartupConfig.PORTAL_GUN_RANGE.get(), 1.0F, false);
        boolean placed = hit instanceof BlockHitResult block && hit.getType() == HitResult.Type.BLOCK && RiftPortal.place(level, player, id, slot, portalColor(stack, slot), block);

        if (!placed) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 1.6F);
            return;
        }

        if (!free) {
            RiftEnergyItem.setEnergy(stack, RiftEnergyItem.getEnergy(stack) - cost);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 0.8F, slot == FIRST_PORTAL ? 1.3F : 0.9F);
    }
}
