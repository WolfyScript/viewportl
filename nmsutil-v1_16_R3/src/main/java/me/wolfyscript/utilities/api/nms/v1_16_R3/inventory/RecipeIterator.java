package me.wolfyscript.utilities.api.nms.v1_16_R3.inventory;

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import net.minecraft.server.v1_16_R3.IRecipe;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.Recipes;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class RecipeIterator implements Iterator<Recipe> {

    private final Iterator<IRecipe<?>> recipes;

    public RecipeIterator(RecipeType recipeType) {
        this.recipes = MinecraftServer.getServer().getCraftingManager().recipes.get(Recipes.a(recipeType.toString().toLowerCase(Locale.ROOT))).values().iterator();
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
