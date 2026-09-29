package com.benbenlaw.rifts.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record RiftRecipe(ItemStackTemplate chargeItem, ResourceKey<Level> fromDimension, ResourceKey<Level> toDimension,
                         int startupTime, int openTime, int closingWarningTime, RiftColorSet activeColors, RiftColorSet closingColors, int riftEnergyPerTick) implements Recipe<RecipeInput> {

    public record RiftColor(float r, float g, float b) {
        public static final Codec<RiftColor> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("r").forGetter(RiftColor::r),
                        Codec.FLOAT.fieldOf("g").forGetter(RiftColor::g),
                        Codec.FLOAT.fieldOf("b").forGetter(RiftColor::b)
                ).apply(instance, RiftColor::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, RiftColor> STREAM_CODEC = StreamCodec.of(
                (buffer, color) -> {
                    buffer.writeFloat(color.r);
                    buffer.writeFloat(color.g);
                    buffer.writeFloat(color.b);
                },
                buffer -> new RiftColor(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
        );

        public float[] toArray() {
            return new float[]{r, g, b};
        }
    }

    public record RiftColorSet(RiftColor base, RiftColor bright, RiftColor boltCore) {
        public static final Codec<RiftColorSet> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        RiftColor.CODEC.fieldOf("base").forGetter(RiftColorSet::base),
                        RiftColor.CODEC.fieldOf("bright").forGetter(RiftColorSet::bright),
                        RiftColor.CODEC.fieldOf("boltCore").forGetter(RiftColorSet::boltCore)
                ).apply(instance, RiftColorSet::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, RiftColorSet> STREAM_CODEC = StreamCodec.of(
                (buffer, set) -> {
                    RiftColor.STREAM_CODEC.encode(buffer, set.base);
                    RiftColor.STREAM_CODEC.encode(buffer, set.bright);
                    RiftColor.STREAM_CODEC.encode(buffer, set.boltCore);
                },
                buffer -> new RiftColorSet(
                        RiftColor.STREAM_CODEC.decode(buffer),
                        RiftColor.STREAM_CODEC.decode(buffer),
                        RiftColor.STREAM_CODEC.decode(buffer)
                )
        );
    }

    public static final MapCodec<RiftRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemStackTemplate.CODEC.fieldOf("charge_item").forGetter(RiftRecipe::chargeItem),
                    Level.RESOURCE_KEY_CODEC.fieldOf("from_dimension").forGetter(RiftRecipe::fromDimension),
                    Level.RESOURCE_KEY_CODEC.fieldOf("to_dimension").forGetter(RiftRecipe::toDimension),
                    Codec.INT.fieldOf("startup_time").forGetter(RiftRecipe::startupTime),
                    Codec.INT.fieldOf("open_time").forGetter(RiftRecipe::openTime),
                    Codec.INT.fieldOf("closing_warning_time").forGetter(RiftRecipe::closingWarningTime),
                    RiftColorSet.CODEC.fieldOf("active_colors").forGetter(RiftRecipe::activeColors),
                    RiftColorSet.CODEC.fieldOf("closing_colors").forGetter(RiftRecipe::closingColors),
                    Codec.INT.fieldOf("rift_energy_per_tick").forGetter(RiftRecipe::riftEnergyPerTick)
            ).apply(instance, RiftRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftRecipe> STREAM_CODEC = StreamCodec.of(
            RiftRecipe::write, RiftRecipe::read);

    public static final RecipeType<RiftRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<RiftRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static RiftRecipe read(RegistryFriendlyByteBuf buffer) {
        ItemStackTemplate chargeItem = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        ResourceKey<Level> fromDimension = ResourceKey.streamCodec(Registries.DIMENSION).decode(buffer);
        ResourceKey<Level> toDimension = ResourceKey.streamCodec(Registries.DIMENSION).decode(buffer);
        int startupTime = buffer.readInt();
        int openTime = buffer.readInt();
        int closingWarningTime = buffer.readInt();
        RiftColorSet activeColors = RiftColorSet.STREAM_CODEC.decode(buffer);
        RiftColorSet closingColors = RiftColorSet.STREAM_CODEC.decode(buffer);
        int riftEnergyPerTick = buffer.readInt();
        return new RiftRecipe(chargeItem, fromDimension, toDimension, startupTime, openTime, closingWarningTime, activeColors, closingColors, riftEnergyPerTick);
    }

    private static void write(RegistryFriendlyByteBuf buffer, RiftRecipe recipe) {
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.chargeItem);
        ResourceKey.streamCodec(Registries.DIMENSION).encode(buffer, recipe.fromDimension);
        ResourceKey.streamCodec(Registries.DIMENSION).encode(buffer, recipe.toDimension);
        buffer.writeInt(recipe.startupTime);
        buffer.writeInt(recipe.openTime);
        buffer.writeInt(recipe.closingWarningTime);
        RiftColorSet.STREAM_CODEC.encode(buffer, recipe.activeColors);
        RiftColorSet.STREAM_CODEC.encode(buffer, recipe.closingColors);
        buffer.writeInt(recipe.riftEnergyPerTick);
    }

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return true;
    }

    @Override
    public @NonNull ItemStack assemble(RecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
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