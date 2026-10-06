package com.benbenlaw.rifts.data.custom;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.recipe.CrusherRecipe;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CrusherRecipeBuilder implements RecipeBuilder {

    private static final int DEFAULT_PROCESSING_TIME = 100;
    private static final int DEFAULT_ENERGY_PER_TICK = 80;

    protected String group;
    protected final SizedIngredient ingredient;
    protected final SizedIngredient output;
    protected Optional<CrusherRecipe.BonusOutput> bonus = Optional.empty();
    protected int processingTime = DEFAULT_PROCESSING_TIME;
    protected int riftEnergyPerTick = DEFAULT_ENERGY_PER_TICK;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public CrusherRecipeBuilder(SizedIngredient ingredient, SizedIngredient output) {
        this.ingredient = ingredient;
        this.output = output;
    }

    public static CrusherRecipeBuilder crusherRecipe(SizedIngredient ingredient, SizedIngredient output) {
        return new CrusherRecipeBuilder(ingredient, output);
    }

    public CrusherRecipeBuilder bonus(SizedIngredient item, float chance) {
        this.bonus = Optional.of(new CrusherRecipe.BonusOutput(item, chance));
        return this;
    }

    public CrusherRecipeBuilder processingTime(int ticks) {
        this.processingTime = ticks;
        return this;
    }

    public CrusherRecipeBuilder riftEnergyPerTick(int energy) {
        this.riftEnergyPerTick = energy;
        return this;
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
        ItemStackTemplate first = output.ingredient().items().findFirst()
                .map(holder -> new ItemStackTemplate(holder.value(), output.count()))
                .orElseThrow(() -> new IllegalStateException("Crusher recipes with a tag output need an explicit id"));
        return ResourceKey.create(
                Registries.RECIPE,
                Rifts.identifier("crusher/" + Objects.requireNonNull(first.item().getKey()).identifier().getPath())
        );
    }

    @Override
    public void save(RecipeOutput output, String id) {
        save(output, ResourceKey.create(Registries.RECIPE, Rifts.identifier("crusher/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        CrusherRecipe recipe = new CrusherRecipe(this.ingredient, this.output, this.bonus, this.processingTime, this.riftEnergyPerTick);
        recipeOutput.accept(resourceKey, recipe, builder.build(resourceKey.identifier().withPrefix("recipes/crusher/")));
    }
}
