package com.benbenlaw.rifts.item.energy;

import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface RiftEnergyItem extends IItemExtension {

    int BAR_COLOR = 0x9A4DFF;

    int getMaxEnergy();

    int getEnergyPerDamage();

    static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(RiftsDataComponents.RIFT_ENERGY.get(), 0);
    }

    static void setEnergy(ItemStack stack, int energy) {
        if (energy <= 0) {
            stack.remove(RiftsDataComponents.RIFT_ENERGY.get());
        } else {
            stack.set(RiftsDataComponents.RIFT_ENERGY.get(), energy);
        }
    }

    static boolean isEmpty(ItemStack stack) {
        return stack.getItem() instanceof RiftEnergyItem && getEnergy(stack) <= 0;
    }

    static boolean isBarVisible(ItemStack stack, RiftEnergyItem item) {
        return getEnergy(stack) < item.getMaxEnergy();
    }

    static int getBarWidth(ItemStack stack, RiftEnergyItem item) {
        return Mth.clamp(Math.round(13.0F * getEnergy(stack) / item.getMaxEnergy()), 0, 13);
    }

    @Override
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        if (amount <= 0 || (entity instanceof Player player && player.hasInfiniteMaterials())) return 0;

        setEnergy(stack, Math.max(0, getEnergy(stack) - amount * getEnergyPerDamage()));
        return 0;
    }
}
