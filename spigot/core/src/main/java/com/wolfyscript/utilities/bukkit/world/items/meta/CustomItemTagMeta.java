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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import java.util.Objects;

public class CustomItemTagMeta extends Meta {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("customitem_tag");

    public CustomItemTagMeta() {
        super(KEY);
    }

    @JsonIgnore
    @Override
    public MetaSettings.Option getOption() {
        return super.getOption();
    }

    @JsonIgnore
    @Override
    public void setOption(MetaSettings.Option option) {
        super.setOption(MetaSettings.Option.EXACT);
    }

    @Override
    public boolean check(CustomItem item, ItemBuilder itemOther) {
        var key = item.key();
        var keyOther = CustomItem.getKeyOfItemMeta(itemOther.getItemMeta());
        if (key != null) {
            return keyOther != null && Objects.equals(key, keyOther);
        }
        return keyOther == null;
    }

}
