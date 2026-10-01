package com.benbenlaw.rifts.data;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.data.custom.InfuserRecipeBuilder;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RiftsRecipesProvider extends RecipeProvider {

    public RiftsRecipesProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new RiftsRecipesProvider(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return Rifts.MOD_ID + " Recipes";
        }
    }

    @Override
    protected void buildRecipes() {

        //Displacer
        shaped(RecipeCategory.MISC, RiftsItems.DISPLACER)
                .pattern(" A ")
                .pattern("BCB")
                .pattern(" A ")
                .define('A', Items.SNOWBALL)
                .define('B', Items.CLAY_BALL)
                .define('C', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(output);

        //Rift Pipe
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_PIPE, 8)
                .pattern("AAA")
                .pattern("   ")
                .pattern("AAA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Storage
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_STORAGE)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', ItemTags.BARS)
                .define('C', RiftsBlocks.RIFT_STEEL_BLOCK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Pylons
        shaped(RecipeCategory.MISC, RiftsBlocks.BASIC_RIFT_PYLON)
                .pattern(" A ")
                .pattern("BBB")
                .pattern("CCC")
                .define('A', ItemTags.LIGHTNING_RODS)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_COPPER)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ADVANCED_RIFT_PYLON)
                .pattern(" A ")
                .pattern("BBB")
                .pattern("CCC")
                .define('A', RiftsBlocks.BASIC_RIFT_PYLON)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ELITE_RIFT_PYLON)
                .pattern(" A ")
                .pattern("BBB")
                .pattern("CCC")
                .define('A', RiftsBlocks.ADVANCED_RIFT_PYLON)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ULTIMATE_RIFT_PYLON)
                .pattern(" A ")
                .pattern("BBB")
                .pattern("CCC")
                .define('A', RiftsBlocks.ELITE_RIFT_PYLON)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Steel
        this.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, RiftsItems.RIFT_STEEL_INGOT, RecipeCategory.MISC, RiftsBlocks.RIFT_STEEL_BLOCK, "rift_steel_ingot_from_rift_steel_block_block", "misc");
        this.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, RiftsItems.RIFT_STEEL_NUGGET, RecipeCategory.MISC, RiftsItems.RIFT_STEEL_INGOT, "rift_steel_nugget_from_rift_steel_block_block", "misc");

        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsItems.RIFT_STEEL_NUGGET.get()), 10, SizedIngredient.of(Items.IRON_NUGGET, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_NUGGET))
                .save(output, "rift_steel_nugget_from_rift_steel_block_block");

        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsItems.RIFT_STEEL_INGOT.get()), 90, SizedIngredient.of(Items.IRON_INGOT, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "rift_steel_ingot_from_rift_steel_block_block");

        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsBlocks.RIFT_STEEL_BLOCK.get().asItem()), 810, SizedIngredient.of(Items.IRON_BLOCK, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output,  "rift_steel_block_from_rift_steel_block_block");

    }
}
