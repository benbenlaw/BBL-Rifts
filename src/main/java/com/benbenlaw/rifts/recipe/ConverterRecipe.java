package com.benbenlaw.rifts.recipe;

import com.benbenlaw.rifts.recipe.util.BlockTarget;
import com.benbenlaw.rifts.recipe.util.BlockTargetCodec;
import com.benbenlaw.rifts.recipe.util.BlockStateResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record ConverterRecipe(BlockTarget input, BlockStateResult output, boolean ignoreBlockState, int processingTime, int riftEnergyPerTick,
                              Optional<ResourceKey<Level>> fromDimension, Optional<ResourceKey<Level>> toDimension) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<ConverterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockTargetCodec.CODEC.fieldOf("input").forGetter(ConverterRecipe::input),
            BlockStateResult.CODEC.codec().fieldOf("output").forGetter(ConverterRecipe::output),
            Codec.BOOL.optionalFieldOf("ignore_block_state", true).forGetter(ConverterRecipe::ignoreBlockState),
            Codec.INT.fieldOf("processing_time").forGetter(ConverterRecipe::processingTime),
            Codec.INT.fieldOf("rift_energy_per_tick").forGetter(ConverterRecipe::riftEnergyPerTick),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("from_dimension").forGetter(ConverterRecipe::fromDimension),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("to_dimension").forGetter(ConverterRecipe::toDimension)
    ).apply(instance, ConverterRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConverterRecipe> STREAM_CODEC = StreamCodec.of(
            ConverterRecipe::write, ConverterRecipe::read);

    public static final RecipeType<ConverterRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<ConverterRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static ConverterRecipe read(RegistryFriendlyByteBuf buffer) {
        BlockTarget input = BlockTargetCodec.STREAM_CODEC.decode(buffer);
        BlockStateResult output = BlockStateResult.STREAM_CODEC.decode(buffer);
        boolean ignoreBlockState = buffer.readBoolean();
        int processingTime = buffer.readVarInt();
        int riftEnergyPerTick = buffer.readVarInt();
        Optional<ResourceKey<Level>> fromDimension = buffer.readBoolean() ? Optional.of(buffer.readResourceKey(Registries.DIMENSION)) : Optional.empty();
        Optional<ResourceKey<Level>> toDimension = buffer.readBoolean() ? Optional.of(buffer.readResourceKey(Registries.DIMENSION)) : Optional.empty();
        return new ConverterRecipe(input, output, ignoreBlockState, processingTime, riftEnergyPerTick, fromDimension, toDimension);
    }

    private static void write(RegistryFriendlyByteBuf buffer, ConverterRecipe recipe) {
        BlockTargetCodec.STREAM_CODEC.encode(buffer, recipe.input);
        BlockStateResult.STREAM_CODEC.encode(buffer, recipe.output);
        buffer.writeBoolean(recipe.ignoreBlockState);
        buffer.writeVarInt(recipe.processingTime);
        buffer.writeVarInt(recipe.riftEnergyPerTick);
        buffer.writeBoolean(recipe.fromDimension.isPresent());
        recipe.fromDimension.ifPresent(buffer::writeResourceKey);
        buffer.writeBoolean(recipe.toDimension.isPresent());
        recipe.toDimension.ifPresent(buffer::writeResourceKey);
    }

    public boolean matchesBlock(BlockState state) {
        return input.matches(state, ignoreBlockState);
    }

    public boolean allowsDimensions(ResourceKey<Level> home, ResourceKey<Level> destination) {
        return fromDimension.map(home::equals).orElse(true) && toDimension.map(destination::equals).orElse(true);
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return false;
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
