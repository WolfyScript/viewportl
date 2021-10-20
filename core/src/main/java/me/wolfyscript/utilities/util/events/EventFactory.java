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

package me.wolfyscript.utilities.util.events;

import me.wolfyscript.utilities.api.inventory.custom_items.ArmorType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EventFactory {

    private EventFactory() {
    }

    public static ArmorEquipEvent createArmorEquipEvent(Player player, ArmorEquipEvent.EquipMethod equipType, ArmorType type, ItemStack oldArmorPiece, ItemStack newArmorPiece) {
        var equipEvent = new ArmorEquipEvent(player, equipType, type, oldArmorPiece, newArmorPiece);
        Bukkit.getPluginManager().callEvent(equipEvent);
        return equipEvent;
    }

    public static ArmorEquipEvent createArmorEquipEvent(Player player, ArmorEquipEvent.EquipMethod equipType, ArmorType type, ItemStack oldArmorPiece, ItemStack newArmorPiece, CustomItem oldCustomArmorPiece, CustomItem newCustomArmorPiece) {
        var equipEvent = new ArmorEquipEvent(player, equipType, type, oldArmorPiece, newArmorPiece, oldCustomArmorPiece, newCustomArmorPiece);
        Bukkit.getPluginManager().callEvent(equipEvent);
        return equipEvent;
    }
}
