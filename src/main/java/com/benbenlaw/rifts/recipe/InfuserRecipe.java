package com.benbenlaw.rifts.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record InfuserRecipe(NonNullList<SizedIngredient> ingredients, ItemStackTemplate output, int riftEnergyPerTick) implements Recipe<InfuserRecipeInput> {

    public static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.list(SizedIngredient.NESTED_CODEC).fieldOf("ingredients").flatXmap(sizedIngredients -> {
                        NonNullList<SizedIngredient> nonNullList = NonNullList.create();
                        nonNullList.addAll(sizedIngredients);
                        return DataResult.success(nonNullList);
                    }, DataResult::success).forGetter(InfuserRecipe::ingredients),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(InfuserRecipe::output),
                    Codec.INT.fieldOf("rift_energy_per_tick").forGetter(InfuserRecipe::riftEnergyPerTick)
            ).apply(instance, InfuserRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodec.of(
            InfuserRecipe::write, InfuserRecipe::read);

    public static final RecipeType<InfuserRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<InfuserRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static InfuserRecipe read(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readInt();
        NonNullList<SizedIngredient> sizedIngredients = NonNullList.create();
        for (int i = 0; i < size; i++) {
            sizedIngredients.add(SizedIngredient.STREAM_CODEC.decode(buffer));
        }
        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        int riftEnergyPerTick = buffer.readInt();

        return new InfuserRecipe(sizedIngredients, output, riftEnergyPerTick);
    }

    private static void write(RegistryFriendlyByteBuf buffer, InfuserRecipe recipe) {
        buffer.writeVarInt(recipe.ingredients().size());
        for (SizedIngredient ingredient : recipe.ingredients()) {
            SizedIngredient.STREAM_CODEC.encode(buffer, ingredient);
        }
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        buffer.writeInt(recipe.riftEnergyPerTick);
    }

    public int[] consumption(InfuserRecipeInput input) {
        int slots = 4;
        int[] remaining = new int[slots];
        for (int slot = 0; slot < slots; slot++) {
            remaining[slot] = input.getItem(slot).getCount();
        }
        int[] consumed = new int[slots];

        for (SizedIngredient ingredient : ingredients) {
            int needed = ingredient.count();
            for (int slot = 0; slot < slots && needed > 0; slot++) {
                if (remaining[slot] > 0 && ingredient.ingredient().test(input.getItem(slot))) {
                    int taken = Math.min(needed, remaining[slot]);
                    remaining[slot] -= taken;
                    consumed[slot] += taken;
                    needed -= taken;
                }
            }
            if (needed > 0) return null;
        }

        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) continue;
            boolean matchesAnIngredient = false;
            for (SizedIngredient ingredient : ingredients) {
                if (ingredient.ingredient().test(stack)) {
                    matchesAnIngredient = true;
                    break;
                }
            }
            if (!matchesAnIngredient) return null;
        }

        return consumed;
    }

    @Override
    public boolean matches(@NotNull InfuserRecipeInput input, @NotNull Level level) {
        return consumption(input) != null;
    }
    @Override
    public @NonNull ItemStack assemble(InfuserRecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<InfuserRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<InfuserRecipeInput>> getType() {
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
