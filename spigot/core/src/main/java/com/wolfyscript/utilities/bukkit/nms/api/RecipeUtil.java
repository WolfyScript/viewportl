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

package com.wolfyscript.utilities.bukkit.nms.api;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.RecipeType;
import com.wolfyscript.utilities.bukkit.nms.inventory.NMSInventoryUtils;
import java.util.Iterator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public abstract class RecipeUtil extends UtilComponent {

    protected RecipeUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Get the Iterator of the specific recipe type.
     * Other than the Bukkit Recipe Iterator this Iterator only contains the recipes of the specified type.
     *
     * @param recipeType The recipe type to get the iterator for.
     * @return The iterator of the recipe type.
     */
    public abstract @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType);

    public void setCurrentRecipe(Inventory inventory, BukkitNamespacedKey recipeId) {
        NMSInventoryUtils.setCurrentRecipe(inventory, recipeId);
    }

    public void setCurrentRecipe(InventoryView view, BukkitNamespacedKey recipeId) {
        NMSInventoryUtils.setCurrentRecipe(view.getTopInventory(), recipeId);
    }
}
