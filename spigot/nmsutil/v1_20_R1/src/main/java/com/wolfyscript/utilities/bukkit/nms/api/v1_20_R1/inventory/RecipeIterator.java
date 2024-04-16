/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.bukkit.nms.api.v1_20_R1.inventory;

import com.wolfyscript.utilities.bukkit.nms.api.inventory.RecipeType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;

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
