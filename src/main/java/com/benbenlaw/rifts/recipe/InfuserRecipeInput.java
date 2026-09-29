package com.benbenlaw.rifts.recipe;

import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.rifts.block.capability.SimpleRiftEnergyHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public class InfuserRecipeInput implements RecipeInput {

    private final SyncableItemHandler handler;
    private final SimpleRiftEnergyHandler energyHandler;

    public InfuserRecipeInput(SyncableItemHandler handler, SimpleRiftEnergyHandler energyHandler) {
        this.handler = handler;
        this.energyHandler = energyHandler;
    }

    @Override
    public ItemStack getItem(int i) {
        return ItemUtil.getStack(handler, i);
    }

    @Override
    public int size() {
        return handler.size();
    }

    public SimpleRiftEnergyHandler getEnergyHandler() {
        return energyHandler;
    }
}
