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

    public int[] matchSlots(InfuserRecipeInput input) {
        boolean[] usedSlots = new boolean[4];
        int[] assignment = new int[ingredients.size()];

        for (int i = 0; i < ingredients.size(); i++) {
            SizedIngredient ingredient = ingredients.get(i);
            int foundSlot = -1;
            for (int slot = 0; slot < 4; slot++) {
                if (usedSlots[slot]) continue;
                if (ingredient.test(input.getItem(slot))) {
                    foundSlot = slot;
                    break;
                }
            }
            if (foundSlot == -1) return null;
            usedSlots[foundSlot] = true;
            assignment[i] = foundSlot;
        }

        for (int slot = 0; slot < 4; slot++) {
            if (!usedSlots[slot] && !input.getItem(slot).isEmpty()) {
                return null;
            }
        }

        return assignment;
    }

    @Override
    public boolean matches(@NotNull InfuserRecipeInput input, @NotNull Level level) {
        return matchSlots(input) != null;
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
