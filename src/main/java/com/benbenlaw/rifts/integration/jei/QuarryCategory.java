package com.benbenlaw.rifts.integration.jei;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.event.client.ClientRecipeCache;
import com.benbenlaw.rifts.recipe.QuarryRecipe;
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

    private static final int COLUMNS = 6;
    private static final int FOCUS_X = 4;
    private static final int ARROW_X = 28;
    private static final int RESULTS_X = 58;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int SLOT = 18;
    private static final int PADDING = 4;
    private static final int WIDTH = RESULTS_X + COLUMNS * SLOT + PADDING;

    private final IDrawable icon;
    private final int height;

    public QuarryCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RiftsBlocks.RIFT_QUARRY.get()));

        // Tall enough to show every result of the largest recipe
        int mostResults = ClientRecipeCache.getQuarryRecipes().values().stream()
                .mapToInt(recipe -> recipe.validResults().size()).max().orElse(COLUMNS);
        int rows = Math.max(2, (mostResults + COLUMNS - 1) / COLUMNS);
        this.height = rows * SLOT + PADDING * 2;
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
        return height;
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
        int centreY = (height - SLOT) / 2;

        builder.addSlot(RecipeIngredientRole.INPUT, FOCUS_X, centreY)
                .setStandardSlotBackground()
                .addItemStacks(recipe.focus().ingredient().items()
                        .map(holder -> new ItemStack(holder.value(), recipe.focus().count()))
                        .toList());

        int total = recipe.totalChance();
        List<QuarryRecipe.Result> results = recipe.validResults();
        for (int i = 0; i < results.size(); i++) {
            QuarryRecipe.Result result = results.get(i);
            double percent = total <= 0 ? 0 : result.chance() * 100.0 / total;

            builder.addSlot(RecipeIngredientRole.OUTPUT, RESULTS_X + (i % COLUMNS) * SLOT, PADDING + (i / COLUMNS) * SLOT)
                    .setStandardSlotBackground()
                    .add(result.resolve())
                    .addRichTooltipCallback((slotView, tooltip) ->
                            tooltip.add(Component.translatable("jei.rifts.chance", formatPercent(percent)).withStyle(ChatFormatting.GOLD)));
        }
    }

    // Whole percentages read as in the cloche (50%), odd weights keep one decimal (7.5%)
    private static String formatPercent(double percent) {
        return Math.abs(percent - Math.rint(percent)) < 0.05 ? String.valueOf((long) Math.rint(percent)) : String.format(Locale.ROOT, "%.1f", percent);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, QuarryJeiRecipe jeiRecipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(ARROW_X, (height - ARROW_HEIGHT) / 2);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, QuarryJeiRecipe jeiRecipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int arrowY = (height - ARROW_HEIGHT) / 2;
        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH && mouseY >= arrowY && mouseY < arrowY + ARROW_HEIGHT) {
            QuarryRecipe recipe = jeiRecipe.recipe();
            tooltip.add(Component.translatable("jei.rifts.quarry.energy", recipe.riftEnergyPerTick(), recipe.processingTime()));
        }
    }
}
