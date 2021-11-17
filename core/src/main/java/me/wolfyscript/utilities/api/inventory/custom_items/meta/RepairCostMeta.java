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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RepairCostMeta extends Meta {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("repair_cost");

    public RepairCostMeta() {
        super(KEY);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(CustomItem item, ItemBuilder itemOther) {
        ItemMeta metaOther = itemOther.getItemMeta();
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Repairable) {
            if (metaOther instanceof Repairable) {
                return switch (option) {
                    case EXACT -> ((Repairable) metaOther).getRepairCost() == ((Repairable) meta).getRepairCost();
                    case LOWER -> ((Repairable) metaOther).getRepairCost() < ((Repairable) meta).getRepairCost();
                    case HIGHER -> ((Repairable) metaOther).getRepairCost() > ((Repairable) meta).getRepairCost();
                    default -> false;
                };
            }
            return false;
        }
        return !(metaOther instanceof Repairable);
    }
}
