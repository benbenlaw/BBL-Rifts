package com.benbenlaw.rifts.recipe;

import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

public class PortalGunRecipe extends NormalCraftingRecipe {

    public static final MapCodec<PortalGunRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                    CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
                    ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
            ).apply(instance, PortalGunRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PortalGunRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
            ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
            ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
            PortalGunRecipe::new);

    public static final RecipeSerializer<PortalGunRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private static final int FIRST_DYE_SLOT = 0;
    private static final int SECOND_DYE_SLOT = 2;

    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate result;

    public PortalGunRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo);
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    public RecipeSerializer<PortalGunRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(pattern.ingredients());
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!pattern.matches(input)) return false;

        DyeColor first = DyeColor.getColor(input.getItem(FIRST_DYE_SLOT));
        DyeColor second = DyeColor.getColor(input.getItem(SECOND_DYE_SLOT));
        return first != null && second != null && first != second;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack stack = result.create();
        DyeColor first = DyeColor.getColor(input.getItem(FIRST_DYE_SLOT));
        DyeColor second = DyeColor.getColor(input.getItem(SECOND_DYE_SLOT));
        if (first != null && second != null) {
            stack.set(RiftsDataComponents.PORTAL_GUN_FIRST_COLOR.get(), first);
            stack.set(RiftsDataComponents.PORTAL_GUN_SECOND_COLOR.get(), second);
        }
        return stack;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapedCraftingRecipeDisplay(
                pattern.width(),
                pattern.height(),
                pattern.ingredients().stream().map(ingredient -> ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }
}
