package com.benbenlaw.rifts.data;

import com.benbenlaw.core.util.CoreTags;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.data.custom.CrusherRecipeBuilder;
import com.benbenlaw.rifts.data.custom.InfuserRecipeBuilder;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
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

    private void ore(TagKey<Item> ore, Item result, int count, float bonusChance, String name) {
        ore(ore, SizedIngredient.of(result, count), SizedIngredient.of(result, 1), bonusChance, name);
    }

    private void ore(TagKey<Item> ore, TagKey<Item> result, int count, float bonusChance, String name) {
        ore(ore, new SizedIngredient(tag(result), count), new SizedIngredient(tag(result), 1), bonusChance, name,
                new NotCondition(new TagEmptyCondition<>(ore)), new NotCondition(new TagEmptyCondition<>(result)));
    }

    private void ore(TagKey<Item> ore, SizedIngredient result, SizedIngredient bonus, float bonusChance, String name, ICondition... conditions) {
        CrusherRecipeBuilder.crusherRecipe(new SizedIngredient(tag(ore), 1), result)
                .bonus(bonus, bonusChance)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output.withConditions(conditions), name);
    }

    @Override
    protected void buildRecipes() {

        //Scanner
        shaped(RecipeCategory.MISC, RiftsItems.RIFT_SCANNER)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', RiftsItems.RIFT_STEEL_NUGGET)
                .define('B', RiftsItems.RIFT_PEARL)
                .unlockedBy("has_rift_pearl", has(RiftsItems.RIFT_PEARL))
                .save(output);

        //Rift Pearl
        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsItems.RIFT_PEARL.get()), 25, SizedIngredient.of(Items.ENDER_PEARL, 1))
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(output, "infuser/rift_pearl_from_ender_pearl");


        //Wrench
        shaped(RecipeCategory.MISC, RiftsItems.RIFT_WRENCH)
                .pattern(" A ")
                .pattern(" BA")
                .pattern("A  ")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', RiftsItems.RIFT_STEEL_NUGGET)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

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

        //Tick Accelerators
        shaped(RecipeCategory.MISC, RiftsBlocks.BASIC_TICK_ACCELERATOR)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Items.REDSTONE)
                .define('C', Items.CLOCK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ADVANCED_TICK_ACCELERATOR)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', RiftsBlocks.BASIC_TICK_ACCELERATOR)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ELITE_TICK_ACCELERATOR)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', RiftsBlocks.ADVANCED_TICK_ACCELERATOR)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ULTIMATE_TICK_ACCELERATOR)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Tags.Items.INGOTS_NETHERITE)
                .define('C', RiftsBlocks.ELITE_TICK_ACCELERATOR)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Steel Gear
        shaped(RecipeCategory.COMBAT, RiftsItems.RIFT_STEEL_SWORD)
                .pattern("A")
                .pattern("A")
                .pattern("S")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, RiftsItems.RIFT_STEEL_PICKAXE)
                .pattern("AAA")
                .pattern(" S ")
                .pattern(" S ")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, RiftsItems.RIFT_STEEL_AXE)
                .pattern("AA")
                .pattern("AS")
                .pattern(" S")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, RiftsItems.RIFT_STEEL_SHOVEL)
                .pattern("A")
                .pattern("S")
                .pattern("S")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, RiftsItems.RIFT_STEEL_HOE)
                .pattern("AA")
                .pattern(" S")
                .pattern(" S")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.COMBAT, RiftsItems.RIFT_STEEL_SPEAR)
                .pattern("  A")
                .pattern(" S ")
                .pattern("S  ")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.COMBAT, RiftsItems.RIFT_STEEL_HELMET)
                .pattern("AAA")
                .pattern("A A")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.COMBAT, RiftsItems.RIFT_STEEL_CHESTPLATE)
                .pattern("A A")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.COMBAT, RiftsItems.RIFT_STEEL_LEGGINGS)
                .pattern("AAA")
                .pattern("A A")
                .pattern("A A")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.COMBAT, RiftsItems.RIFT_STEEL_BOOTS)
                .pattern("A A")
                .pattern("A A")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Charger
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_CHARGER)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Tags.Items.INGOTS_COPPER)
                .define('C', Items.REDSTONE_BLOCK)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Crusher
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_CRUSHER)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Items.FLINT)
                .define('C', Items.PISTON)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Crushing
        ore(Tags.Items.ORES_IRON, CoreTags.Items.commonTag("dusts/iron"), 2, 0.5F, "iron_dust_from_iron_ore");
        ore(Tags.Items.ORES_GOLD, CoreTags.Items.commonTag("dusts/gold"), 2, 0.25F, "gold_dust_from_gold_ore");
        ore(Tags.Items.ORES_COPPER, CoreTags.Items.commonTag("dusts/copper"), 4, 0.5F, "copper_dust_from_copper_ore");
        ore(Tags.Items.ORES_COAL, Items.COAL, 2, 0.25F, "coal_from_coal_ore");
        ore(Tags.Items.ORES_REDSTONE, Items.REDSTONE, 8, 0.5F, "redstone_from_redstone_ore");
        ore(Tags.Items.ORES_LAPIS, Items.LAPIS_LAZULI, 10, 0.5F, "lapis_from_lapis_ore");

        ore(CoreTags.Items.commonTag("ores/tin"), CoreTags.Items.commonTag("dusts/tin"), 2, 0.5F, "tin_dust_from_tin_ore");

        CrusherRecipeBuilder.crusherRecipe(SizedIngredient.of(Items.STONE, 1), SizedIngredient.of(Items.COBBLESTONE, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "cobblestone_from_stone");
        CrusherRecipeBuilder.crusherRecipe(SizedIngredient.of(Items.COBBLESTONE, 1), SizedIngredient.of(Items.GRAVEL, 1))
                .bonus(SizedIngredient.of(Items.FLINT, 1), 0.1F)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "gravel_from_cobblestone");
        CrusherRecipeBuilder.crusherRecipe(SizedIngredient.of(Items.GRAVEL, 1), SizedIngredient.of(Items.SAND, 1))
                .bonus(SizedIngredient.of(Items.FLINT, 1), 0.2F)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "sand_from_gravel");

        //Rift Furnace
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_FURNACE)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("CCC")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Items.FURNACE)
                .define('C', Tags.Items.INGOTS_COPPER)
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
        this.nineBlockStorageRecipes(RecipeCategory.MISC, RiftsItems.RIFT_STEEL_INGOT, RecipeCategory.MISC, RiftsBlocks.RIFT_STEEL_BLOCK,
                Rifts.MOD_ID + ":rift_steel_block", null, Rifts.MOD_ID + ":rift_steel_ingot_from_rift_steel_block", "misc");
        this.nineBlockStorageRecipes(RecipeCategory.MISC, RiftsItems.RIFT_STEEL_NUGGET, RecipeCategory.MISC, RiftsItems.RIFT_STEEL_INGOT,
                Rifts.MOD_ID + ":rift_steel_ingot", null, Rifts.MOD_ID + ":rift_steel_nugget_from_rift_steel_ingot", "misc");

        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsItems.RIFT_STEEL_NUGGET.get()), 10, SizedIngredient.of(Items.IRON_NUGGET, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_NUGGET))
                .save(output, "infuser/rift_steel_nugger");

        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsItems.RIFT_STEEL_INGOT.get()), 90, SizedIngredient.of(Items.IRON_INGOT, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "infuser/rift_steel_ingot");

        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsBlocks.RIFT_STEEL_BLOCK.get().asItem()), 810, SizedIngredient.of(Items.IRON_BLOCK, 1))
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output,  "infuser/rift_steel_block");

        //Rift Pearl
        InfuserRecipeBuilder.infuserRecipe(new ItemStackTemplate(RiftsItems.RIFT_PEARL.get().asItem()), 1000, SizedIngredient.of(Items.ENDER_PEARL, 1))
                .unlockedBy("has_rift_steel", has(Items.ENDER_PEARL))
                .save(output,  "infuser/rift_pearl");

        //Rift
        shapeless(RecipeCategory.MISC, RiftsBlocks.RIFT_PLANKS, 4).requires(RiftsBlocks.RIFT_LOG).unlockedBy("has_rift_log", has(RiftsBlocks.RIFT_LOG)).save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_PLANK_STAIRS, 4)
                .pattern("A  ")
                .pattern("AA ")
                .pattern("AAA")
                .define('A', RiftsBlocks.RIFT_PLANKS)
                .unlockedBy("has_rift_planks", has(RiftsBlocks.RIFT_PLANKS))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_PLANK_SLAB, 6)
                .pattern("AAA")
                .define('A', RiftsBlocks.RIFT_PLANKS)
                .unlockedBy("has_rift_planks", has(RiftsBlocks.RIFT_PLANKS))
                .save(output);
    }
}
