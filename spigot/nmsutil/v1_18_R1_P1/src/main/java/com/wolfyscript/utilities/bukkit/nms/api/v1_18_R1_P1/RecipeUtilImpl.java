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

package com.wolfyscript.utilities.bukkit.nms.api.v1_18_R1_P1;

import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.RecipeUtil;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.RecipeType;
import com.wolfyscript.utilities.bukkit.nms.api.v1_18_R1_P1.inventory.RecipeIterator;
import org.bukkit.inventory.Recipe;
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

}
