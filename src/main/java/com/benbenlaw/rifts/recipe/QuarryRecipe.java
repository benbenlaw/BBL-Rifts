package com.benbenlaw.rifts.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record QuarryRecipe(SizedIngredient focus, List<Result> results, int processingTime, int riftEnergyPerTick) implements Recipe<SingleRecipeInput> {

    public record Result(Optional<SizedIngredient> item, int chance) {
        public static final Codec<Result> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SizedIngredient.NESTED_CODEC.lenientOptionalFieldOf("item").forGetter(Result::item),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("chance").forGetter(Result::chance)
        ).apply(instance, Result::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.of(
                (buffer, result) -> {
                    buffer.writeBoolean(result.item().isPresent());
                    result.item().ifPresent(item -> SizedIngredient.STREAM_CODEC.encode(buffer, item));
                    buffer.writeVarInt(result.chance());
                },
                buffer -> new Result(buffer.readBoolean() ? Optional.of(SizedIngredient.STREAM_CODEC.decode(buffer)) : Optional.empty(), buffer.readVarInt())
        );

        // Tag results resolve to the first item in the tag; empty when the tag has no items (e.g. the other mod is missing)
        public ItemStack resolve() {
            return item.map(CrusherRecipe::resolve).orElse(ItemStack.EMPTY);
        }
    }

    public static final MapCodec<QuarryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.NESTED_CODEC.fieldOf("focus").forGetter(QuarryRecipe::focus),
            Codec.list(Result.CODEC).fieldOf("results").forGetter(QuarryRecipe::results),
            Codec.INT.fieldOf("processing_time").forGetter(QuarryRecipe::processingTime),
            Codec.INT.fieldOf("rift_energy_per_tick").forGetter(QuarryRecipe::riftEnergyPerTick)
    ).apply(instance, QuarryRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryRecipe> STREAM_CODEC = StreamCodec.of(
            QuarryRecipe::write, QuarryRecipe::read);

    public static final RecipeType<QuarryRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<QuarryRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static QuarryRecipe read(RegistryFriendlyByteBuf buffer) {
        SizedIngredient focus = SizedIngredient.STREAM_CODEC.decode(buffer);
        int size = buffer.readVarInt();
        List<Result> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            results.add(Result.STREAM_CODEC.decode(buffer));
        }
        int processingTime = buffer.readVarInt();
        int riftEnergyPerTick = buffer.readVarInt();
        return new QuarryRecipe(focus, results, processingTime, riftEnergyPerTick);
    }

    private static void write(RegistryFriendlyByteBuf buffer, QuarryRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.focus);
        buffer.writeVarInt(recipe.results.size());
        for (Result result : recipe.results) {
            Result.STREAM_CODEC.encode(buffer, result);
        }
        buffer.writeVarInt(recipe.processingTime);
        buffer.writeVarInt(recipe.riftEnergyPerTick);
    }

    // Results whose tag is empty are left out of the roll and the totals
    public List<Result> validResults() {
        return results.stream().filter(result -> !result.resolve().isEmpty()).toList();
    }

    public int totalChance() {
        return validResults().stream().mapToInt(Result::chance).sum();
    }

    // Picks one result with probability chance / total chance
    public ItemStack roll(RandomSource random) {
        List<Result> valid = validResults();
        int total = valid.stream().mapToInt(Result::chance).sum();
        if (total <= 0) return ItemStack.EMPTY;

        int roll = random.nextInt(total);
        for (Result result : valid) {
            roll -= result.chance();
            if (roll < 0) return result.resolve();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return focus.ingredient().test(input.item());
    }

    @Override
    public @NonNull ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
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
