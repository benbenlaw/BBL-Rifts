package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.recipe.InfuserRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfuserCategory implements IRecipeCategory<InfuserJeiRecipe> {

    public static final IRecipeType<InfuserJeiRecipe> RECIPE_TYPE =
            IRecipeType.create(Rifts.identifier("infuser"), InfuserJeiRecipe.class);

    private static final int WIDTH = 98;
    private static final int HEIGHT = 44;
    private static final int ARROW_X = 42;
    private static final int ARROW_Y = 12;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int[][][] INPUT_LAYOUTS = {
            {{13, 13}},
            {{4, 13}, {22, 13}},
            {{4, 4}, {22, 4}, {13, 22}},
            {{4, 4}, {22, 4}, {4, 22}, {22, 22}}
    };

    private final IDrawable icon;

    public InfuserCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RiftsBlocks.RIFT_INFUSER.get()));
    }

    @Override
    public @NotNull IRecipeType<InfuserJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.rifts.infuser");
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
    public @Nullable Identifier getIdentifier(InfuserJeiRecipe recipe) {
        return recipe.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserJeiRecipe jeiRecipe, IFocusGroup focuses) {
        InfuserRecipe recipe = jeiRecipe.recipe();

        List<SizedIngredient> ingredients = recipe.ingredients();
        int count = Math.min(ingredients.size(), INPUT_LAYOUTS.length);
        for (int i = 0; i < count; i++) {
            SizedIngredient sized = ingredients.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_LAYOUTS[count - 1][i][0], INPUT_LAYOUTS[count - 1][i][1])
                    .setStandardSlotBackground()
                    .addItemStacks(sized.ingredient().items()
                            .map(holder -> new ItemStack(holder.value(), sized.count()))
                            .toList());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 13)
                .setStandardSlotBackground()
                .add(recipe.output().create());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, InfuserJeiRecipe jeiRecipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(ARROW_X, ARROW_Y);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, InfuserJeiRecipe jeiRecipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH && mouseY >= ARROW_Y && mouseY < ARROW_Y + ARROW_HEIGHT) {
            tooltip.add(Component.translatable("jei.rifts.infuser.energy", jeiRecipe.recipe().riftEnergyPerTick()));
        }
    }
}
