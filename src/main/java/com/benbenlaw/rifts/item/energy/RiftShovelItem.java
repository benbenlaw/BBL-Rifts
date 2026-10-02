package com.benbenlaw.rifts.item.energy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;

public class RiftShovelItem extends ShovelItem implements RiftToolEnergy {

    public RiftShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
        super(material, attackDamage, attackSpeed, properties);
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
