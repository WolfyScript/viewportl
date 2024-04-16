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

package com.wolfyscript.utilities.bukkit.nms.api.v1_18_R2;

import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.RecipeUtil;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.RecipeType;
import com.wolfyscript.utilities.bukkit.nms.api.v1_18_R2.inventory.RecipeIterator;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RecipeUtilImpl extends RecipeUtil {

    protected RecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }

//    @Override
//    public FunctionalFurnaceRecipe furnaceRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
//        return null;
//    }
//
//    @Override
//    public FunctionalCampfireRecipe campfireRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
//        return null;
//    }
//
//    @Override
//    public FunctionalBlastingRecipe blastingRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
//        return null;//new FunctionalBlastingRecipeImpl(key, group, toNMS(source, true), CraftItemStack.asNMSCopy(result), experience, cookingTime, recipeMatch);
//    }
//
//    @Override
//    public FunctionalSmokingRecipe smokingRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
//        return null;
//    }
//
//    @Override
//    public void registerCookingRecipe(FunctionalRecipe recipe) {
//        if (recipe instanceof net.minecraft.world.item.crafting.Recipe<?> mcRecipe) {
//            MinecraftServer.getServer().getRecipeManager().addRecipe(mcRecipe);
//        }
//    }

    public Ingredient toNMS(RecipeChoice bukkit, boolean requireNotEmpty) {
        Ingredient stack;
        if (bukkit == null) {
            stack = Ingredient.EMPTY;
        } else if (bukkit instanceof RecipeChoice.MaterialChoice) {
            stack = new Ingredient(((RecipeChoice.MaterialChoice)bukkit).getChoices().stream().map((mat) -> new Ingredient.ItemValue(CraftItemStack.asNMSCopy(new ItemStack(mat)))));
        } else {
            if (!(bukkit instanceof RecipeChoice.ExactChoice)) {
                throw new IllegalArgumentException("Unknown recipe stack instance " + bukkit);
            }
            stack = new Ingredient(((RecipeChoice.ExactChoice)bukkit).getChoices().stream().map((mat) -> new Ingredient.ItemValue(CraftItemStack.asNMSCopy(mat))));
            stack.exact = true;
        }

        stack.dissolve();
        if (requireNotEmpty && stack.itemStacks.length == 0) {
            throw new IllegalArgumentException("Recipe requires at least one non-air choice!");
        } else {
            return stack;
        }
    }

}
