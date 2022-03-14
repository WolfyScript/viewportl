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

package me.wolfyscript.utilities.util.inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains enums for the creative menu tabs.
 * Each enum contains the corresponding Materials of that tab.
 */
public enum CreativeModeTab {

    BREWING,
    BUILDING_BLOCKS,
    DECORATIONS,
    COMBAT,
    TOOLS,
    REDSTONE,
    FOOD,
    TRANSPORTATION,
    MISC,
    SEARCH;

    private static boolean register = true;
    @JsonIgnore
    private final Set<Material> materials;

    CreativeModeTab() {
        this.materials = new HashSet<>();
    }

    public static boolean isValid(Material material, CreativeModeTab creativeModeTab) {
        return creativeModeTab.isValid(material);
    }

    public boolean isValid(Material material) {
        return materials.contains(material);
    }

    public static CreativeModeTab getCategory(Material material) {
        for (CreativeModeTab tab : values()) {
            if (tab.isValid(material)) return tab;
        }
        return SEARCH;
    }

    public static void init() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading Creative Mode Tabs");
        WolfyUtilities.getWUCore().getNmsUtil().getInventoryUtil().initItemCategories();
        register = false;
    }

    public Set<Material> getMaterials() {
        return new HashSet<>(materials);
    }

    public void registerMaterial(Material material) {
        if (register) {
            materials.add(material);
        }
    }
}
