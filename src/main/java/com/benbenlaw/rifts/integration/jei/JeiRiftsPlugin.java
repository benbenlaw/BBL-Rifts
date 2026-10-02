package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.datamaps.DisplacerConversions;
import com.benbenlaw.rifts.datamaps.RiftsDataMaps;
import com.benbenlaw.rifts.event.client.ClientRecipeCache;
import com.benbenlaw.rifts.item.RiftsItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class JeiRiftsPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return Rifts.identifier("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DisplacerConversionCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CrusherCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(DisplacerConversionCategory.RECIPE_TYPE, new ItemStack(RiftsItems.DISPLACER.get()));
        registration.addCraftingStation(RecipeTypes.SMELTING, new ItemStack(RiftsBlocks.RIFT_FURNACE.get()));
        registration.addCraftingStation(CrusherCategory.RECIPE_TYPE, new ItemStack(RiftsBlocks.RIFT_CRUSHER.get()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CrusherCategory.RECIPE_TYPE, ClientRecipeCache.getCrusherRecipes().entrySet().stream()
                .map(entry -> new CrusherJeiRecipe(entry.getKey(), entry.getValue()))
                .toList());

        DisplacerConversions config = RiftsItems.DISPLACER.get().builtInRegistryHolder().getData(RiftsDataMaps.DISPLACER_HIT_RESULTS);
        if (config == null) return;

        List<DisplacerConversionRecipe> recipes = new ArrayList<>();

        for (Map.Entry<Block, Block> entry : config.conversions().entrySet()) {
            ItemStack input = displayStack(entry.getKey());
            ItemStack output = displayStack(entry.getValue());
            if (input.isEmpty() || output.isEmpty()) continue;

            recipes.add(new DisplacerConversionRecipe("block/" + BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString().replace(':', '/'),
                    List.of(input), output));
        }

        for (Map.Entry<TagKey<Block>, Block> entry : config.tagConversions().entrySet()) {
            List<ItemStack> inputs = new ArrayList<>();
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(entry.getKey())) {
                ItemStack stack = displayStack(holder.value());
                if (!stack.isEmpty()) inputs.add(stack);
            }
            ItemStack output = displayStack(entry.getValue());
            if (inputs.isEmpty() || output.isEmpty()) continue;

            recipes.add(new DisplacerConversionRecipe("tag/" + entry.getKey().location().toString().replace(':', '/'),
                    inputs, output));
        }

        for (Map.Entry<EntityType<?>, EntityType<?>> entry : config.entityConversions().entrySet()) {
            ItemStack input = spawnEgg(entry.getKey());
            ItemStack output = spawnEgg(entry.getValue());
            if (input.isEmpty() || output.isEmpty()) continue;

            recipes.add(new DisplacerConversionRecipe("entity/" + BuiltInRegistries.ENTITY_TYPE.getKey(entry.getKey()).toString().replace(':', '/'),
                    List.of(input), output));
        }

        registration.addRecipes(DisplacerConversionCategory.RECIPE_TYPE, recipes);
    }

    private static ItemStack displayStack(Block block) {
        ItemStack stack = new ItemStack(block.asItem());
        if (!stack.isEmpty()) return stack;
        Level level = Minecraft.getInstance().level;
        if (level == null) return ItemStack.EMPTY;
        return block.getCloneItemStack(level, BlockPos.ZERO, block.defaultBlockState(), false, null);
    }

    private static ItemStack spawnEgg(EntityType<?> type) {
        return SpawnEggItem.byId(type).map(holder -> new ItemStack(holder.value())).orElse(ItemStack.EMPTY);
    }
}
