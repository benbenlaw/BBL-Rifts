package com.benbenlaw.rifts.event.client;

import com.benbenlaw.rifts.recipe.CrusherRecipe;
import com.benbenlaw.rifts.recipe.InfuserRecipe;
import com.benbenlaw.rifts.recipe.QuarryRecipe;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ClientRecipeCache {

    private static Map<Identifier, CrusherRecipe> crusherRecipes = new HashMap<>();

    private static Map<Identifier, InfuserRecipe> infuserRecipes = new HashMap<>();

    public static void setInfuserRecipes(Map<Identifier, InfuserRecipe> recipes) {
        infuserRecipes = recipes;
    }

    public static Map<Identifier, InfuserRecipe> getInfuserRecipes() {
        return infuserRecipes;
    }

    private static Map<Identifier, QuarryRecipe> quarryRecipes = new HashMap<>();

    public static void setQuarryRecipes(Map<Identifier, QuarryRecipe> recipes) {
        quarryRecipes = recipes;
    }

    public static Map<Identifier, QuarryRecipe> getQuarryRecipes() {
        return quarryRecipes;
    }

    public static void setCrusherRecipes(Map<Identifier, CrusherRecipe> recipes) {
        crusherRecipes = recipes;
    }

    public static Map<Identifier, CrusherRecipe> getCrusherRecipes() {
        return crusherRecipes;
    }
}
