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


import com.google.common.collect.Multimap;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

public class AttributesModifiersMeta extends Meta {

    public AttributesModifiersMeta() {
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta metaOther = itemOther.getItemMeta();
        ItemMeta meta = item.getItemMeta();
        if (option.equals(MetaSettings.Option.IGNORE)) {
            if (metaOther.hasAttributeModifiers()) {
                Multimap<Attribute, AttributeModifier> modifiers = metaOther.getAttributeModifiers();
                modifiers.keySet().forEach(metaOther::removeAttributeModifier);
            }
            if (meta.hasAttributeModifiers()) {
                Multimap<Attribute, AttributeModifier> modifiers = metaOther.getAttributeModifiers();
                modifiers.keySet().forEach(metaOther::removeAttributeModifier);
            }
        }
        itemOther.setItemMeta(metaOther);
        item.setItemMeta(meta);
        return true;
    }
}
