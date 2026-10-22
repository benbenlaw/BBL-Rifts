package com.benbenlaw.rifts.data.custom;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.recipe.QuarryRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuarryRecipeBuilder implements RecipeBuilder {

    private static final int DEFAULT_PROCESSING_TIME = 200;
    private static final int DEFAULT_ENERGY_PER_TICK = 100;

    protected String group;
    protected final SizedIngredient focus;
    protected final List<QuarryRecipe.Result> results = new ArrayList<>();
    protected int processingTime = DEFAULT_PROCESSING_TIME;
    protected int riftEnergyPerTick = DEFAULT_ENERGY_PER_TICK;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public QuarryRecipeBuilder(SizedIngredient focus) {
        this.focus = focus;
    }

    public static QuarryRecipeBuilder quarryRecipe(SizedIngredient focus) {
        return new QuarryRecipeBuilder(focus);
    }

    public QuarryRecipeBuilder result(SizedIngredient item, int chance) {
        this.results.add(new QuarryRecipe.Result(java.util.Optional.of(item), chance));
        return this;
    }

    public QuarryRecipeBuilder processingTime(int ticks) {
        this.processingTime = ticks;
        return this;
    }

    public QuarryRecipeBuilder riftEnergyPerTick(int energy) {
        this.riftEnergyPerTick = energy;
        return this;
    }

    @Override
    public @NotNull QuarryRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull QuarryRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        ItemStackTemplate first = focus.ingredient().items().findFirst()
                .map(holder -> new ItemStackTemplate(holder.value()))
                .orElseThrow(() -> new IllegalStateException("Quarry recipes with a tag focus need an explicit id"));
        return ResourceKey.create(Registries.RECIPE,
                Rifts.identifier("quarry/" + Objects.requireNonNull(first.item().getKey()).identifier().getPath()));
    }

    @Override
    public void save(RecipeOutput output, String id) {
        save(output, ResourceKey.create(Registries.RECIPE, Rifts.identifier("quarry/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        QuarryRecipe recipe = new QuarryRecipe(this.focus, List.copyOf(this.results), this.processingTime, this.riftEnergyPerTick);
        recipeOutput.accept(resourceKey, recipe, builder.build(resourceKey.identifier().withPrefix("recipes/quarry/")));
    }
}
