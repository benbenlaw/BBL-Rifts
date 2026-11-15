package com.benbenlaw.rifts.recipe;

import com.benbenlaw.core.recipe.ChanceResult;
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

public record QuarryRecipe(SizedIngredient focus, List<ChanceResult> results, int processingTime, int riftEnergyPerTick) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<QuarryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.NESTED_CODEC.fieldOf("focus").forGetter(QuarryRecipe::focus),
            Codec.list(ChanceResult.CODEC).fieldOf("results").forGetter(QuarryRecipe::results),
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
        List<ChanceResult> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            results.add(ChanceResult.read(buffer));
        }
        int processingTime = buffer.readVarInt();
        int riftEnergyPerTick = buffer.readVarInt();
        return new QuarryRecipe(focus, results, processingTime, riftEnergyPerTick);
    }

    private static void write(RegistryFriendlyByteBuf buffer, QuarryRecipe recipe) {
        SizedIngredient.STREAM_CODEC.encode(buffer, recipe.focus);
        buffer.writeVarInt(recipe.results.size());
        for (ChanceResult result : recipe.results) {
            result.write(buffer);
        }
        buffer.writeVarInt(recipe.processingTime);
        buffer.writeVarInt(recipe.riftEnergyPerTick);
    }

    public float totalChance() {
        float total = 0;
        for (ChanceResult result : results) {
            total += Math.max(0, result.chance());
        }
        return total;
    }

    public ItemStack roll(RandomSource random) {
        float total = totalChance();
        if (total <= 0) return ItemStack.EMPTY;

        float roll = random.nextFloat() * total;
        for (ChanceResult result : results) {
            roll -= Math.max(0, result.chance());
            if (roll < 0) return result.template().create();
        }
        return results.getLast().template().create();
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
