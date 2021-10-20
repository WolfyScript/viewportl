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

package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

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

}
