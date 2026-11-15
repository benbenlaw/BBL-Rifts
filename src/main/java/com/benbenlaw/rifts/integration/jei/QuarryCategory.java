package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.core.recipe.ChanceResult;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.recipe.QuarryRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class QuarryCategory implements IRecipeCategory<QuarryJeiRecipe> {

    public static final IRecipeType<QuarryJeiRecipe> RECIPE_TYPE =
            IRecipeType.create(Rifts.identifier("quarry"), QuarryJeiRecipe.class);

    private static final int COLUMNS = 4;
    private static final int VISIBLE_ROWS = 2;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int FOCUS_X = 2;
    private static final int ARROW_X = 22;
    private static final int RESULTS_X = 46;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int SLOT = 18;
    private static final int PADDING = 2;
    private static final int WIDTH = RESULTS_X + COLUMNS * SLOT + SCROLL_BAR_WIDTH + PADDING;
    private static final int HEIGHT = VISIBLE_ROWS * SLOT + PADDING * 2;

    private final IDrawable icon;

    public QuarryCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RiftsBlocks.RIFT_QUARRY.get()));
    }

    @Override
    public @NotNull IRecipeType<QuarryJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.rifts.quarry");
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
    public @Nullable Identifier getIdentifier(QuarryJeiRecipe recipe) {
        return recipe.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, QuarryJeiRecipe jeiRecipe, IFocusGroup focuses) {
        QuarryRecipe recipe = jeiRecipe.recipe();
        int centreY = (HEIGHT - SLOT) / 2;

        builder.addSlot(RecipeIngredientRole.INPUT, FOCUS_X, centreY)
                .setStandardSlotBackground()
                .addItemStacks(recipe.focus().ingredient().items()
                        .map(holder -> new ItemStack(holder.value(), recipe.focus().count()))
                        .toList());

        float total = recipe.totalChance();
        List<ChanceResult> results = recipe.results();
        for (int i = 0; i < results.size(); i++) {
            ChanceResult result = results.get(i);
            double percent = total <= 0 ? 0 : result.chance() * 100.0 / total;

            boolean visible = i < COLUMNS * VISIBLE_ROWS;
            int slotX = visible ? RESULTS_X + (i % COLUMNS) * SLOT : RESULTS_X;
            int slotY = visible ? PADDING + (i / COLUMNS) * SLOT : PADDING;

            builder.addSlot(RecipeIngredientRole.OUTPUT, slotX, slotY)
                    .setStandardSlotBackground()
                    .add(result.template().create())
                    .addRichTooltipCallback((slotView, tooltip) ->
                            tooltip.add(Component.translatable("jei.rifts.chance", formatPercent(percent)).withStyle(ChatFormatting.GOLD)));
        }
    }

    private static String formatPercent(double percent) {
        return Math.abs(percent - Math.rint(percent)) < 0.05 ? String.valueOf((long) Math.rint(percent)) : String.format(Locale.ROOT, "%.1f", percent);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, QuarryJeiRecipe jeiRecipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(ARROW_X, (HEIGHT - ARROW_HEIGHT) / 2);

        List<IRecipeSlotDrawable> outputs = builder.getRecipeSlots().getSlots(RecipeIngredientRole.OUTPUT);
        if (outputs.size() > COLUMNS * VISIBLE_ROWS) {
            builder.addScrollGridWidget(outputs, COLUMNS, VISIBLE_ROWS).setPosition(RESULTS_X, PADDING);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, QuarryJeiRecipe jeiRecipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int arrowY = (HEIGHT - ARROW_HEIGHT) / 2;
        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH && mouseY >= arrowY && mouseY < arrowY + ARROW_HEIGHT) {
            QuarryRecipe recipe = jeiRecipe.recipe();
            tooltip.add(Component.translatable("jei.rifts.quarry.energy", recipe.riftEnergyPerTick(), recipe.processingTime()));
        }
    }
}
