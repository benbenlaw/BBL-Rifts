package com.benbenlaw.rifts.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record CrusherRecipe(SizedIngredient ingredient, ItemStackTemplate output, Optional<BonusOutput> bonus,
                            int processingTime, int riftEnergyPerTick) implements Recipe<SingleRecipeInput> {

    public record BonusOutput(ItemStackTemplate item, float chance) {
        public static final Codec<BonusOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStackTemplate.CODEC.fieldOf("item").forGetter(BonusOutput::item),
                Codec.floatRange(0F, 1F).fieldOf("chance").forGetter(BonusOutput::chance)
        ).apply(instance, BonusOutput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BonusOutput> STREAM_CODEC = StreamCodec.of(
                (buffer, bonus) -> {
                    ItemStackTemplate.STREAM_CODEC.encode(buffer, bonus.item());
                    buffer.writeFloat(bonus.chance());
                },
                buffer -> new BonusOutput(ItemStackTemplate.STREAM_CODEC.decode(buffer), buffer.readFloat())
        );
    }

    public static final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.NESTED_CODEC.fieldOf("ingredient").forGetter(CrusherRecipe::ingredient),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(CrusherRecipe::output),
            BonusOutput.CODEC.optionalFieldOf("bonus").forGetter(CrusherRecipe::bonus),
            Codec.INT.fieldOf("processing_time").forGetter(CrusherRecipe::processingTime),
            Codec.INT.fieldOf("rift_energy_per_tick").forGetter(CrusherRecipe::riftEnergyPerTick)
    ).apply(instance, CrusherRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC = StreamCodec.of(
            CrusherRecipe::write, CrusherRecipe::read);

    public static final RecipeType<CrusherRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<CrusherRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static CrusherRecipe read(RegistryFriendlyByteBuf buffer) {
        SizedIngredient ingredient = SizedIngredient.STREAM_CODEC.decode(buffer);
        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        Optional<BonusOutput> bonus = buffer.readBoolean() ? Optional.of(BonusOutput.STREAM_CODEC.decode(buffer)) : Optional.empty();
        int processingTime = buffer.readInt();
        int riftEnergyPerTick = buffer.readInt();
        return new CrusherRecipe(ingredient, output, bonus, processingTime, riftEnergyPerTick);
    }

    private static void write(RegistryFriendlyByteBuf buffer, CrusherRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.ingredient);
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        buffer.writeBoolean(recipe.bonus.isPresent());
        recipe.bonus.ifPresent(bonus -> BonusOutput.STREAM_CODEC.encode(buffer, bonus));
        buffer.writeInt(recipe.processingTime);
        buffer.writeInt(recipe.riftEnergyPerTick);
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public @NonNull ItemStack assemble(SingleRecipeInput input) {
        return output.create();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }
}
