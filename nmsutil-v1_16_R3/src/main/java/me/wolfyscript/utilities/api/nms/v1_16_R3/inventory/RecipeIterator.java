package me.wolfyscript.utilities.api.nms.v1_16_R3.inventory;

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import net.minecraft.server.v1_16_R3.IRecipe;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.Recipes;
import org.bukkit.inventory.Recipe;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RecipeIterator implements Iterator<Recipe> {

    private final Iterator<IRecipe<?>> recipes;

    public RecipeIterator(RecipeType recipeType) {
        Recipes<?> recipesReg = getRecipes(recipeType);
        if (recipesReg != null) {
            this.recipes = MinecraftServer.getServer().getCraftingManager().recipes.get(recipesReg).values().iterator();
        } else {
            this.recipes = Collections.emptyIterator();
        }
    }

    private Recipes<?> getRecipes(RecipeType type) {
        switch (type) {
            case CRAFTING:
                return Recipes.CRAFTING;
            case SMELTING:
                return Recipes.SMELTING;
            case BLASTING:
                return Recipes.BLASTING;
            case SMOKING:
                return Recipes.SMOKING;
            case CAMPFIRE_COOKING:
                return Recipes.CAMPFIRE_COOKING;
            case STONECUTTING:
                return Recipes.STONECUTTING;
            case SMITHING:
                return Recipes.SMITHING;
            default:
                return null;
        }
    }

    public RecipeIterator(List<IRecipe<?>> recipeList) {
        this.recipes = recipeList.iterator();
    }

    @Override
    public boolean hasNext() {
        return this.recipes.hasNext();
    }

    @Override
    public Recipe next() {
        return this.recipes.next().toBukkitRecipe();
    }

    @Override
    public void remove() {
        this.recipes.remove();
    }
}
