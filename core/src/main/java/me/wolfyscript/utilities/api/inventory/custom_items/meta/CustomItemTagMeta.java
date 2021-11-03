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

package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class CustomItemTagMeta extends Meta {

    public static final NamespacedKey namespacedKey = new NamespacedKey("wolfyutilities", "custom_item");

    public CustomItemTagMeta() {
        setOption(MetaSettings.Option.IGNORE);
        setAvailableOptions(MetaSettings.Option.IGNORE, MetaSettings.Option.EXACT);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta meta1 = item.getItemMeta();
        ItemMeta meta2 = itemOther.getItemMeta();
        if (hasKey(meta1)) {
            return hasKey(meta2) && Objects.equals(getKey(meta1), getKey(meta2));
        }
        return !hasKey(meta2);
    }

    private boolean hasKey(ItemMeta meta) {
        return meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING);
    }

    private me.wolfyscript.utilities.util.NamespacedKey getKey(ItemMeta meta) {
        return me.wolfyscript.utilities.util.NamespacedKey.of(meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING));
    }
}
