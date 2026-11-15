package com.benbenlaw.rifts.integration.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record DisplacerConversionRecipe(String name, ItemStack displacer, List<ItemStack> inputs, ItemStack output) {
}
