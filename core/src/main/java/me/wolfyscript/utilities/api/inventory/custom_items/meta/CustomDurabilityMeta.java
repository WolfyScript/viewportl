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

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomDurabilityMeta extends Meta {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("custom_durability");

    public CustomDurabilityMeta() {
        super(KEY);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(CustomItem item, ItemBuilder itemOther) {
        boolean meta0 = item.hasCustomDurability();
        boolean meta1 = itemOther.hasCustomDurability();
        if (meta0 && meta1) {
            return switch (option) {
                case EXACT -> itemOther.getCustomDurability() == item.getCustomDurability();
                case LOWER -> itemOther.getCustomDurability() < item.getCustomDurability();
                case HIGHER -> itemOther.getCustomDurability() > item.getCustomDurability();
                default -> false;
            };
        } else return !meta0 && !meta1;
    }
}
