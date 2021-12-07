package me.wolfyscript.utilities.api.nms.v1_17_R1_P1.inventory;

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RecipeIterator implements Iterator<org.bukkit.inventory.Recipe> {

    private final Iterator<Recipe<?>> recipes;

    public RecipeIterator(RecipeType recipeType) {
        net.minecraft.world.item.crafting.RecipeType<?> recipesReg = getRecipes(recipeType);
        if (recipesReg != null) {
            this.recipes = MinecraftServer.getServer().getRecipeManager().recipes.get(recipesReg).values().iterator();
        } else {
            this.recipes = Collections.emptyIterator();
        }
    }

    public RecipeIterator(List<Recipe<?>> recipeList) {
        this.recipes = recipeList.iterator();
    }

    private net.minecraft.world.item.crafting.RecipeType<?> getRecipes(RecipeType type) {
        return switch (type) {
            case CRAFTING -> net.minecraft.world.item.crafting.RecipeType.CRAFTING;
            case SMELTING -> net.minecraft.world.item.crafting.RecipeType.SMELTING;
            case BLASTING -> net.minecraft.world.item.crafting.RecipeType.BLASTING;
            case SMOKING -> net.minecraft.world.item.crafting.RecipeType.SMOKING;
            case CAMPFIRE_COOKING -> net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING;
            case STONECUTTING -> net.minecraft.world.item.crafting.RecipeType.STONECUTTING;
            case SMITHING -> net.minecraft.world.item.crafting.RecipeType.SMITHING;
        };
    }

    @Override
    public boolean hasNext() {
        return this.recipes.hasNext();
    }

    @Override
    public org.bukkit.inventory.Recipe next() {
        return this.recipes.next().toBukkitRecipe();
    }

    @Override
    public void remove() {
        this.recipes.remove();
    }
}
