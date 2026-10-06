package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.recipe.CrusherRecipe;
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

public class CrusherCategory implements IRecipeCategory<CrusherJeiRecipe> {

    public static final IRecipeType<CrusherJeiRecipe> RECIPE_TYPE =
            IRecipeType.create(Rifts.identifier("crusher"), CrusherJeiRecipe.class);

    private static final int WIDTH = 100;
    private static final int HEIGHT = 44;

    private final IDrawable icon;

    public CrusherCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RiftsBlocks.RIFT_CRUSHER.get()));
    }

    @Override
    public @NotNull IRecipeType<CrusherJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.rifts.crusher");
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
    public @Nullable Identifier getIdentifier(CrusherJeiRecipe recipe) {
        return recipe.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrusherJeiRecipe jeiRecipe, IFocusGroup focuses) {
        CrusherRecipe recipe = jeiRecipe.recipe();

        builder.addSlot(RecipeIngredientRole.INPUT, 4, 14)
                .setStandardSlotBackground()
                .addItemStacks(recipe.ingredient().ingredient().items()
                        .map(holder -> new ItemStack(holder.value(), recipe.ingredient().count()))
                        .toList());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 4)
                .setStandardSlotBackground()
                .add(CrusherRecipe.resolve(recipe.output()));

        recipe.bonus().ifPresent(bonus -> builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 24)
                .setStandardSlotBackground()
                .add(CrusherRecipe.resolve(bonus.item()))
                .addRichTooltipCallback((slotView, tooltip) ->
                        tooltip.add(Component.translatable("jei.rifts.crusher.bonus_chance", Math.round(bonus.chance() * 100)))));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CrusherJeiRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(34, 14);
    }
}
