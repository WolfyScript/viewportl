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

package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import org.bukkit.inventory.ItemStack;

public interface CustomStack {

    ItemStack getItemStack();

    String getNamespace();

    String getId();

    String getNamespacedID();

    String getPermission();

    boolean hasPermission();

    boolean isBlockAllEnchants();

    boolean hasUsagesAttribute();

    void setUsages(int amount);

    void reduceUsages(int amount);

    int getUsages();

    boolean hasCustomDurability();

    int getDurability();

    void setDurability(int durability);

    int getMaxDurability();

}
