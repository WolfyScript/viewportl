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

package com.wolfyscript.utilities.bukkit.compatibility.plugins;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface EcoIntegration {

    /**
     * The name of the plugin, that this integration belongs to.
     */
    String KEY = "eco";

    /**
     * Checks if the ItemStack is a CustomItem from eco.
     *
     * @param itemStack The bukkit stack to check.
     * @return True if the ItemStack is an eco CustomItem; otherwise false.
     */
    boolean isCustomItem(ItemStack itemStack);

    /**
     * Gets the NamespacedKey of the CustomItem, that belongs to the ItemStack.
     *
     *
     * @param itemStack The ItemStack to get the CustomItem from.
     * @return The NamespacedKey of the CustomItem; null if the ItemStack is not a CustomItem
     */
    @Nullable
    NamespacedKey getCustomItem(ItemStack itemStack);

    /**
     * Gets the CustomItem of the specified key.
     *
     * @param key The key of the CustomItem.
     * @return The ItemStack of the CustomItem.
     */
    ItemStack lookupItem(String key);

    /**
     * Gets the CustomItem of the specified key.
     *
     * @param key The key of the CustomItem.
     * @return The ItemStack of the CustomItem.
     */
    default ItemStack lookupItem(NamespacedKey key) {
        return lookupItem(key.toString());
    }
}
