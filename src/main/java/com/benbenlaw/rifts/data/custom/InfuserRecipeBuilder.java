package com.benbenlaw.rifts.data.custom;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.recipe.InfuserRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class InfuserRecipeBuilder implements RecipeBuilder {

    protected String group;
    protected NonNullList<SizedIngredient> ingredients;
    protected ItemStackTemplate output;
    protected int riftEnergyPerTick;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public InfuserRecipeBuilder(ItemStackTemplate output, int riftEnergyPerTick, SizedIngredient... ingredients) {
        if (ingredients.length == 0 || ingredients.length > 4) {
            throw new IllegalArgumentException("Infuser recipes need between 1 and 4 ingredients, got " + ingredients.length);
        }
        this.ingredients = NonNullList.create();
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.output = output;
        this.riftEnergyPerTick = riftEnergyPerTick;
    }

    public static InfuserRecipeBuilder infuserRecipe(ItemStackTemplate output, int riftEnergyPerTick, SizedIngredient... ingredients) {
        return new InfuserRecipeBuilder(output, riftEnergyPerTick, ingredients);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        ItemStackTemplate stack = this.output;
        return ResourceKey.create(
                Registries.RECIPE,
                Rifts.identifier("infuser/" + Objects.requireNonNull(stack.item().getKey()).identifier().getPath())
        );
    }

    @Override
    public void save(RecipeOutput output, String id) {
        save(output, ResourceKey.create(Registries.RECIPE, Rifts.identifier("infuser/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        InfuserRecipe infuserRecipe = new InfuserRecipe(this.ingredients, this.output, this.riftEnergyPerTick);
        recipeOutput.accept(resourceKey, infuserRecipe, builder.build(resourceKey.identifier().withPrefix("recipes/infuser/")));
    }
}
