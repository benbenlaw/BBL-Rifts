package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.item.RiftsItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisplacerConversionCategory implements IRecipeCategory<DisplacerConversionRecipe> {

    public static final IRecipeType<DisplacerConversionRecipe> RECIPE_TYPE =
            IRecipeType.create(Rifts.identifier("displacer_conversion"), DisplacerConversionRecipe.class);

    private static final int WIDTH = 120;
    private static final int HEIGHT = 26;

    private final IDrawable icon;

    public DisplacerConversionCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RiftsItems.DISPLACER.get()));
    }

    @Override
    public @NotNull IRecipeType<DisplacerConversionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.rifts.displacer_conversions");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable Identifier getIdentifier(DisplacerConversionRecipe recipe) {
        return Rifts.identifier("displacer/" + recipe.name());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DisplacerConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
                .setStandardSlotBackground()
                .add(new ItemStack(RiftsItems.DISPLACER.get()));

        builder.addSlot(RecipeIngredientRole.INPUT, 30, 4)
                .setStandardSlotBackground()
                .addItemStacks(recipe.inputs());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 4)
                .setStandardSlotBackground()
                .add(recipe.output());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, DisplacerConversionRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(62, 5);
    }
}
