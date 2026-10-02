package com.benbenlaw.rifts.item.energy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RiftToolItem extends Item implements RiftToolEnergy {

    public RiftToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return RiftEnergyItem.isBarVisible(stack, this);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return RiftEnergyItem.getBarWidth(stack, this);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }
}
