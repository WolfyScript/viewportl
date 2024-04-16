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

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import org.bukkit.plugin.Plugin;

public class NMSEntry extends NMSUtil {

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the {@link Plugin} parameter.
     *
     * @param wolfyUtilities
     */
    public NMSEntry(WolfyUtilsBukkit wolfyUtilities) {
        super(wolfyUtilities);
        this.blockUtil = new BlockUtilImpl(this);
        this.itemUtil = new ItemUtilImpl(this);
        this.inventoryUtil = new InventoryUtilImpl(this);
        this.nbtUtil = new NBTUtilImpl(this);
        this.recipeUtil = new RecipeUtilImpl(this);
        this.networkUtil = new NetworkUtilImpl(this);
    }

}
