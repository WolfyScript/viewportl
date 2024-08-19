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
package com.wolfyscript.viewportl.spigot.gui.interaction

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class GUIInventoryListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInvClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        if (holder is BukkitInventoryGuiHolder) {
//            bukkitInventoryGuiHolder.guiHolder().getViewManager().blockedByInteraction();
            holder.onClick(event)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onItemDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is BukkitInventoryGuiHolder) {
//            bukkitInventoryGuiHolder.guiHolder().getViewManager().blockedByInteraction();
            holder.onDrag(event)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder is BukkitInventoryGuiHolder) {
//            bukkitInventoryGuiHolder.guiHolder().getViewManager().blockedByInteraction();
            holder.onClose(event)
        }
    }
}
