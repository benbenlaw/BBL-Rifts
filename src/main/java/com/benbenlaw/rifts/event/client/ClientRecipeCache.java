package com.benbenlaw.rifts.event.client;

import com.benbenlaw.rifts.recipe.CrusherRecipe;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ClientRecipeCache {

    private static Map<Identifier, CrusherRecipe> crusherRecipes = new HashMap<>();

    public static void setCrusherRecipes(Map<Identifier, CrusherRecipe> recipes) {
        crusherRecipes = recipes;
    }

    public static Map<Identifier, CrusherRecipe> getCrusherRecipes() {
        return crusherRecipes;
    }
}
