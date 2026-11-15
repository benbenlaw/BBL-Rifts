package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.recipe.ConverterRecipe;
import com.benbenlaw.rifts.recipe.util.BlockTarget;
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
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConverterCategory implements IRecipeCategory<ConverterJeiRecipe> {

    public static final IRecipeType<ConverterJeiRecipe> RECIPE_TYPE =
            IRecipeType.create(Rifts.identifier("converter"), ConverterJeiRecipe.class);

    private static final int WIDTH = 82;
    private static final int HEIGHT = 26;
    private static final int ARROW_X = 30;
    private static final int ARROW_Y = 5;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;

    private final IDrawable icon;

    public ConverterCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RiftsBlocks.RIFT_CONVERTER.get()));
    }

    public static ItemStack displayStack(BlockState state) {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        if (stack.isEmpty() && !state.getFluidState().isEmpty()) {
            stack = new ItemStack(state.getFluidState().getType().getBucket());
        }
        return stack;
    }

    public static List<ItemStack> inputStacks(BlockTarget target) {
        List<ItemStack> stacks = new ArrayList<>();
        if (target instanceof BlockTarget.Single single) {
            stacks.add(displayStack(single.blockState()));
        } else if (target instanceof BlockTarget.Tag tag) {
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag.tag())) {
                stacks.add(displayStack(holder.value().defaultBlockState()));
            }
        }
        stacks.removeIf(ItemStack::isEmpty);
        return stacks;
    }

    @Override
    public @NotNull IRecipeType<ConverterJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.rifts.converter");
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
    public @Nullable Identifier getIdentifier(ConverterJeiRecipe recipe) {
        return recipe.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ConverterJeiRecipe jeiRecipe, IFocusGroup focuses) {
        ConverterRecipe recipe = jeiRecipe.recipe();

        builder.addSlot(RecipeIngredientRole.INPUT, 4, 4)
                .setStandardSlotBackground()
                .addItemStacks(inputStacks(recipe.input()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 4)
                .setStandardSlotBackground()
                .add(displayStack(recipe.output().state()));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ConverterJeiRecipe jeiRecipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(ARROW_X, ARROW_Y);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ConverterJeiRecipe jeiRecipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH && mouseY >= ARROW_Y && mouseY < ARROW_Y + ARROW_HEIGHT) {
            ConverterRecipe recipe = jeiRecipe.recipe();
            tooltip.add(Component.translatable("jei.rifts.converter.energy", recipe.riftEnergyPerTick(), recipe.processingTime()));
            recipe.fromDimension().ifPresent(dimension -> tooltip.add(Component.translatable("jei.rifts.converter.from", dimension.identifier().toString()).withStyle(ChatFormatting.GRAY)));
            recipe.toDimension().ifPresent(dimension -> tooltip.add(Component.translatable("jei.rifts.converter.to", dimension.identifier().toString()).withStyle(ChatFormatting.GRAY)));
        }
    }
}
