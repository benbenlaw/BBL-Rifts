package com.benbenlaw.rifts.data.custom;

import net.minecraft.core.registries.BuiltInRegistries;
import com.benbenlaw.rifts.recipe.util.BlockTarget;
import com.benbenlaw.rifts.recipe.util.BlockStateResult;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.recipe.ConverterRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ConverterRecipeBuilder implements RecipeBuilder {

    private static final int DEFAULT_PROCESSING_TIME = 200;
    private static final int DEFAULT_ENERGY_PER_TICK = 200;

    protected String group;
    protected final BlockTarget input;
    protected final BlockState output;
    protected boolean ignoreBlockState = true;
    protected int processingTime = DEFAULT_PROCESSING_TIME;
    protected int riftEnergyPerTick = DEFAULT_ENERGY_PER_TICK;
    protected Optional<ResourceKey<Level>> fromDimension = Optional.empty();
    protected Optional<ResourceKey<Level>> toDimension = Optional.empty();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public ConverterRecipeBuilder(BlockTarget input, BlockState output) {
        this.input = input;
        this.output = output;
    }

    public static ConverterRecipeBuilder converterRecipe(BlockTarget input, BlockState output) {
        return new ConverterRecipeBuilder(input, output);
    }

    public ConverterRecipeBuilder ignoreBlockState(boolean ignore) {
        this.ignoreBlockState = ignore;
        return this;
    }

    public ConverterRecipeBuilder processingTime(int ticks) {
        this.processingTime = ticks;
        return this;
    }

    public ConverterRecipeBuilder fromDimension(ResourceKey<Level> dimension) {
        this.fromDimension = Optional.of(dimension);
        return this;
    }

    public ConverterRecipeBuilder toDimension(ResourceKey<Level> dimension) {
        this.toDimension = Optional.of(dimension);
        return this;
    }

    public ConverterRecipeBuilder riftEnergyPerTick(int energy) {
        this.riftEnergyPerTick = energy;
        return this;
    }

    @Override
    public @NotNull ConverterRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull ConverterRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE,
                Rifts.identifier("converter/" + BuiltInRegistries.BLOCK.getKey(output.getBlock()).getPath()));
    }

    @Override
    public void save(RecipeOutput output, String id) {
        save(output, ResourceKey.create(Registries.RECIPE, Rifts.identifier("converter/" + id)));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        ConverterRecipe recipe = new ConverterRecipe(this.input, new BlockStateResult(this.output), this.ignoreBlockState, this.processingTime, this.riftEnergyPerTick, this.fromDimension, this.toDimension);
        recipeOutput.accept(resourceKey, recipe, builder.build(resourceKey.identifier().withPrefix("recipes/converter/")));
    }
}
