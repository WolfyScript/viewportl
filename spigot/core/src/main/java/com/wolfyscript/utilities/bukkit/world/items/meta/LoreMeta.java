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

package com.wolfyscript.utilities.bukkit.world.items.meta;


import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import java.util.Objects;

public class LoreMeta extends Meta {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("lore");

    public LoreMeta() {
        super(KEY);
    }

    @Override
    public boolean check(CustomItem item, ItemBuilder itemOther) {
        return Objects.equals(item.getItemMeta().getLore(), itemOther.getItemMeta().getLore());
    }
}
