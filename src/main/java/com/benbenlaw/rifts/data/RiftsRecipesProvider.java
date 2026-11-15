package com.benbenlaw.rifts.data;

import java.util.Map;
import com.benbenlaw.rifts.recipe.PortalGunRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import com.benbenlaw.core.util.CoreTags;
import com.benbenlaw.rifts.recipe.util.BlockTarget;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.data.custom.ConverterRecipeBuilder;
import com.benbenlaw.rifts.data.worldgen.RiftsWorldGen;
import com.benbenlaw.rifts.data.custom.CrusherRecipeBuilder;
import com.benbenlaw.rifts.data.custom.QuarryRecipeBuilder;
import com.benbenlaw.rifts.data.custom.InfuserRecipeBuilder;
import com.benbenlaw.rifts.item.RiftsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
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

    private void portalGun() {
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Rifts.identifier("rift_portal_gun"));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(RiftsItems.RIFT_STEEL_INGOT.get()),
                'B', Ingredient.of(RiftsItems.RIFT_PEARL.get()),
                'C', Ingredient.of(RiftsBlocks.RIFT_STORAGE.asItem()),
                'D', tag(ItemTags.DYES)),
                "DBD", "ACA", " A ");

        PortalGunRecipe recipe = new PortalGunRecipe(new Recipe.CommonInfo(true),
                new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), pattern,
                new ItemStackTemplate(RiftsItems.RIFT_PORTAL_GUN.get()));

        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                .addCriterion("has_rift_pearl", has(RiftsItems.RIFT_PEARL))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(AdvancementRequirements.Strategy.OR);
        output.accept(key, recipe, advancement.build(key.identifier().withPrefix("recipes/misc/")));
    }

    private void focusUpgrade(DeferredItem<Item> result, DeferredItem<Item> previous, TagKey<Item> material) {
        shaped(RecipeCategory.MISC, result)
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', material)
                .define('B', previous)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);
    }

    private QuarryRecipeBuilder quarry(Item focus, int processingTime, int energyPerTick) {
        return QuarryRecipeBuilder.quarryRecipe(SizedIngredient.of(focus, 1))
                .processingTime(processingTime)
                .riftEnergyPerTick(energyPerTick)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT));
    }

    private void planksFrom(DeferredBlock<Block> log, String name) {
        shapeless(RecipeCategory.MISC, RiftsBlocks.RIFT_PLANKS, 4).requires(log)
                .unlockedBy("has_" + name, has(log))
                .save(output, ResourceKey.create(Registries.RECIPE, Rifts.identifier(name)));
    }

    private void stonecutting(DeferredBlock<Block> result, DeferredBlock<Block> base, int count) {
        String name = getItemName(result.get()) + "_from_" + getItemName(base.get()) + "_stonecutting";
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(base.get()), RecipeCategory.BUILDING_BLOCKS, result.get(), count)
                .unlockedBy("has_" + getItemName(base.get()), has(base.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, Rifts.identifier(name)));
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

        //Wrench
        shaped(RecipeCategory.MISC, RiftsItems.RIFT_WRENCH)
                .pattern(" A ")
                .pattern(" BA")
                .pattern("A  ")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', RiftsItems.RIFT_STEEL_NUGGET)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Portal Gun
        portalGun();

        //Displacer
        shaped(RecipeCategory.MISC, RiftsItems.DISPLACER, 5)
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
        shaped(RecipeCategory.MISC, RiftsBlocks.BASIC_RIFT_CRUSHER)
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

        //Rift Converter
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_CONVERTER)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', RiftsItems.RIFT_PEARL)
                .define('C', Items.ENDER_EYE)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Converting
        ConverterRecipeBuilder.converterRecipe(new BlockTarget.Single(Blocks.DIAMOND_BLOCK.defaultBlockState()), Blocks.EMERALD_BLOCK.defaultBlockState())
                .fromDimension(Level.OVERWORLD).toDimension(RiftsWorldGen.RIFT_LEVEL)
                .processingTime(400).riftEnergyPerTick(400)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "emerald_block_from_diamond_block");
        ConverterRecipeBuilder.converterRecipe(new BlockTarget.Single(Blocks.EMERALD_BLOCK.defaultBlockState()), Blocks.DIAMOND_BLOCK.defaultBlockState())
                .fromDimension(RiftsWorldGen.RIFT_LEVEL).toDimension(Level.OVERWORLD)
                .processingTime(400).riftEnergyPerTick(800)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "diamond_block_from_emerald_block");
        ConverterRecipeBuilder.converterRecipe(new BlockTarget.Single(Blocks.IRON_BLOCK.defaultBlockState()), Blocks.GOLD_BLOCK.defaultBlockState())
                .fromDimension(Level.OVERWORLD).toDimension(Level.NETHER)
                .processingTime(300).riftEnergyPerTick(300)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "gold_block_from_iron_block");
        ConverterRecipeBuilder.converterRecipe(new BlockTarget.Single(Blocks.WATER.defaultBlockState()), Blocks.BLUE_ICE.defaultBlockState())
                .ignoreBlockState(false)
                .fromDimension(Level.OVERWORLD).toDimension(RiftsWorldGen.RIFT_LEVEL)
                .processingTime(200).riftEnergyPerTick(200)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output, "blue_ice_from_water");

        //Rift Surge Core
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_SURGE_CORE)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', RiftsBlocks.RIFT_STEEL_BLOCK)
                .define('B', RiftsItems.RIFT_PEARL)
                .define('C', Items.TNT)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Quarry
        shaped(RecipeCategory.MISC, RiftsBlocks.RIFT_QUARRY)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Items.IRON_PICKAXE)
                .define('C', RiftsItems.RIFT_PEARL)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        //Rift Focus
        shaped(RecipeCategory.MISC, RiftsItems.BASIC_RIFT_FOCUS)
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', RiftsItems.RIFT_PEARL)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);
        focusUpgrade(RiftsItems.ADVANCED_RIFT_FOCUS, RiftsItems.BASIC_RIFT_FOCUS, Tags.Items.GEMS_DIAMOND);
        focusUpgrade(RiftsItems.ELITE_RIFT_FOCUS, RiftsItems.ADVANCED_RIFT_FOCUS, Tags.Items.INGOTS_NETHERITE);
        focusUpgrade(RiftsItems.ULTIMATE_RIFT_FOCUS, RiftsItems.ELITE_RIFT_FOCUS, Tags.Items.NETHER_STARS);

        quarry(RiftsItems.BASIC_RIFT_FOCUS.get(), 200, 100)
                .result(new ItemStackTemplate(Items.COBBLESTONE, 4), 40F)
                .result(new ItemStackTemplate(Items.GRAVEL, 2), 20F)
                .result(new ItemStackTemplate(Items.SAND, 2), 20F)
                .result(new ItemStackTemplate(Items.FLINT, 1), 8F)
                .result(new ItemStackTemplate(Items.COAL, 2), 8F)
                .result(new ItemStackTemplate(Items.RAW_IRON, 1), 4F)
                .save(output, "basic_focus");

        quarry(RiftsItems.ADVANCED_RIFT_FOCUS.get(), 160, 200)
                .result(new ItemStackTemplate(Items.RAW_IRON, 2), 30F)
                .result(new ItemStackTemplate(Items.RAW_COPPER, 3), 25F)
                .result(new ItemStackTemplate(Items.COAL, 3), 20F)
                .result(new ItemStackTemplate(Items.REDSTONE, 4), 12F)
                .result(new ItemStackTemplate(Items.LAPIS_LAZULI, 4), 8F)
                .result(new ItemStackTemplate(Items.RAW_GOLD, 1), 5F)
                .save(output, "advanced_focus");

        quarry(RiftsItems.ELITE_RIFT_FOCUS.get(), 120, 400)
                .result(new ItemStackTemplate(Items.RAW_IRON, 3), 25F)
                .result(new ItemStackTemplate(Items.RAW_GOLD, 2), 25F)
                .result(new ItemStackTemplate(Items.REDSTONE, 6), 20F)
                .result(new ItemStackTemplate(Items.LAPIS_LAZULI, 6), 15F)
                .result(new ItemStackTemplate(Items.DIAMOND, 1), 10F)
                .result(new ItemStackTemplate(Items.EMERALD, 1), 5F)
                .save(output, "elite_focus");

        quarry(RiftsItems.ULTIMATE_RIFT_FOCUS.get(), 80, 800)
                .result(new ItemStackTemplate(Items.DIAMOND, 2), 25F)
                .result(new ItemStackTemplate(Items.EMERALD, 2), 20F)
                .result(new ItemStackTemplate(Items.RAW_GOLD, 3), 25F)
                .result(new ItemStackTemplate(Items.QUARTZ, 4), 15F)
                .result(new ItemStackTemplate(Items.NETHERITE_SCRAP, 1), 5F)
                .save(output, "ultimate_focus");

        //Rift Furnace
        shaped(RecipeCategory.MISC, RiftsBlocks.BASIC_RIFT_FURNACE)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("CCC")
                .define('A', RiftsItems.RIFT_STEEL_INGOT)
                .define('B', Items.FURNACE)
                .define('C', Tags.Items.INGOTS_COPPER)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ADVANCED_RIFT_CRUSHER)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("CCC")
                .define('A', RiftsBlocks.BASIC_RIFT_CRUSHER)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ELITE_RIFT_CRUSHER)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("CCC")
                .define('A', RiftsBlocks.ADVANCED_RIFT_CRUSHER)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ULTIMATE_RIFT_CRUSHER)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("CCC")
                .define('A', RiftsBlocks.ELITE_RIFT_CRUSHER)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ADVANCED_RIFT_FURNACE)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("CCC")
                .define('A', RiftsBlocks.BASIC_RIFT_FURNACE)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ELITE_RIFT_FURNACE)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("CCC")
                .define('A', RiftsBlocks.ADVANCED_RIFT_FURNACE)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_rift_steel", has(RiftsItems.RIFT_STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, RiftsBlocks.ULTIMATE_RIFT_FURNACE)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("CCC")
                .define('A', RiftsBlocks.ELITE_RIFT_FURNACE)
                .define('B', RiftsItems.RIFT_STEEL_INGOT)
                .define('C', Tags.Items.INGOTS_NETHERITE)
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
                .save(output, "infuser/rift_steel_nugget");

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

        planksFrom(RiftsBlocks.RIFT_WOOD, "rift_planks_from_rift_wood");
        planksFrom(RiftsBlocks.STRIPPED_RIFT_LOG, "rift_planks_from_stripped_rift_log");
        planksFrom(RiftsBlocks.STRIPPED_RIFT_WOOD, "rift_planks_from_stripped_rift_wood");
        woodFromLogs(RiftsBlocks.RIFT_WOOD, RiftsBlocks.RIFT_LOG);
        woodFromLogs(RiftsBlocks.STRIPPED_RIFT_WOOD, RiftsBlocks.STRIPPED_RIFT_LOG);

        for (var family : RiftsBlockFamilies.ALL) {
            generateRecipes(family, FeatureFlags.REGISTRY.allFlags());
        }

        hangingSign(RiftsBlocks.RIFT_HANGING_SIGN_ITEM.get(), RiftsBlocks.STRIPPED_RIFT_LOG.get());

        shaped(RecipeCategory.BUILDING_BLOCKS, RiftsBlocks.POLISHED_RIFT_STONE, 4)
                .pattern("AA").pattern("AA")
                .define('A', RiftsBlocks.RIFT_STONE)
                .unlockedBy("has_rift_stone", has(RiftsBlocks.RIFT_STONE))
                .save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, RiftsBlocks.RIFT_STONE_BRICKS, 4)
                .pattern("AA").pattern("AA")
                .define('A', RiftsBlocks.POLISHED_RIFT_STONE)
                .unlockedBy("has_polished_rift_stone", has(RiftsBlocks.POLISHED_RIFT_STONE))
                .save(output);

        stonecutting(RiftsBlocks.RIFT_STONE_STAIRS, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_SLAB, RiftsBlocks.RIFT_STONE, 2);
        stonecutting(RiftsBlocks.RIFT_STONE_WALL, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE_STAIRS, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE_SLAB, RiftsBlocks.RIFT_STONE, 2);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE_WALL, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE_STAIRS, RiftsBlocks.POLISHED_RIFT_STONE, 1);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE_SLAB, RiftsBlocks.POLISHED_RIFT_STONE, 2);
        stonecutting(RiftsBlocks.POLISHED_RIFT_STONE_WALL, RiftsBlocks.POLISHED_RIFT_STONE, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICKS, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICKS, RiftsBlocks.POLISHED_RIFT_STONE, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICK_STAIRS, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICK_SLAB, RiftsBlocks.RIFT_STONE, 2);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICK_WALL, RiftsBlocks.RIFT_STONE, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICK_STAIRS, RiftsBlocks.RIFT_STONE_BRICKS, 1);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICK_SLAB, RiftsBlocks.RIFT_STONE_BRICKS, 2);
        stonecutting(RiftsBlocks.RIFT_STONE_BRICK_WALL, RiftsBlocks.RIFT_STONE_BRICKS, 1);
        stonecutting(RiftsBlocks.CHISELED_RIFT_STONE_BRICKS, RiftsBlocks.RIFT_STONE_BRICKS, 1);
        stonecutting(RiftsBlocks.CHISELED_RIFT_STONE_BRICKS, RiftsBlocks.RIFT_STONE, 1);
    }
}
