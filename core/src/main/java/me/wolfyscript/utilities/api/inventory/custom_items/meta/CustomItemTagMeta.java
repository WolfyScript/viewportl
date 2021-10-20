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


import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemTagMeta extends Meta {

    public CustomItemTagMeta() {
        setOption(MetaSettings.Option.IGNORE);
        setAvailableOptions(MetaSettings.Option.IGNORE, MetaSettings.Option.EXACT);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta meta1 = itemOther.getItemMeta();
        ItemMeta meta2 = item.getItemMeta();
        if (option.equals(MetaSettings.Option.IGNORE)) {
            NamespacedKey namespacedKey = new NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
            if (meta1.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                meta1.getPersistentDataContainer().remove(namespacedKey);
            }
            if (meta2.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                meta2.getPersistentDataContainer().remove(namespacedKey);
            }
        }
        itemOther.setItemMeta(meta1);
        item.setItemMeta(meta2);
        return true;
    }
}
