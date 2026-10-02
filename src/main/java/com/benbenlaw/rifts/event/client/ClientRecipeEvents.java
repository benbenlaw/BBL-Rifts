package com.benbenlaw.rifts.event.client;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.recipe.CrusherRecipe;
import com.benbenlaw.rifts.recipe.RiftsRecipeTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Rifts.MOD_ID, value = Dist.CLIENT)
public class ClientRecipeEvents {

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        Map<Identifier, CrusherRecipe> crusherRecipes = new HashMap<>();
        for (RecipeHolder<CrusherRecipe> holder : event.getRecipeMap().byType(RiftsRecipeTypes.CRUSHER_TYPE.get())) {
            crusherRecipes.put(holder.id().identifier(), holder.value());
        }
        ClientRecipeCache.setCrusherRecipes(crusherRecipes);
    }
}
