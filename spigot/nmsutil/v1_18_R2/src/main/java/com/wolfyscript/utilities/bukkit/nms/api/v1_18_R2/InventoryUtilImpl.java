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

import com.wolfyscript.utilities.bukkit.nms.api.InventoryUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.world.inventory.CreativeModeTab;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public final void initItemCategories() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            var item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                var creativeModeTab = item.getItemCategory();
                if (creativeModeTab != null) {
                    CreativeModeTab.of(creativeModeTab.getRecipeFolderName()).ifPresent(tab-> tab.registerMaterial(material));
                }
            }
        }
    }
}
