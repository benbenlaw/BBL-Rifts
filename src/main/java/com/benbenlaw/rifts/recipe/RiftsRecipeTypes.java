package com.benbenlaw.rifts.recipe;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RiftsRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Rifts.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Rifts.MOD_ID);

    //Rift
    public static final Supplier<RecipeSerializer<RiftRecipe>> RIFT_SERIALIZER =
            SERIALIZER.register("rift", () -> RiftRecipe.SERIALIZER);
    public static final Supplier<RecipeType<RiftRecipe>> RIFT_TYPE =
            TYPES.register("rift", () -> RiftRecipe.TYPE);

    //Infuser
    public static final Supplier<RecipeSerializer<InfuserRecipe>> INFUSER_SERIALIZER =
            SERIALIZER.register("infuser", () -> InfuserRecipe.SERIALIZER);
    public static final Supplier<RecipeType<InfuserRecipe>> INFUSER_TYPE =
            TYPES.register("infuser", () -> InfuserRecipe.TYPE);

}
