/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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
package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.BukkitInventoryGuiHolder
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class InventoryUIListener(val runtime: ViewRuntime, val interactionHandler: SpigotLikeInvUIInteractionHandler) : Listener {

    private fun withHolder(holder: InventoryHolder?, fn: GuiHolder.() -> Unit) {
        if (holder is BukkitInventoryGuiHolder && holder.guiHolder.viewManager == runtime) {
            fn(holder.guiHolder)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInvClick(event: InventoryClickEvent) {
        withHolder(event.inventory.holder) {
            val valueHandler = ValueHandler()
            interactionHandler.onClick(InventoryUIInteractionContext(runtime, event, valueHandler))
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onItemDrag(event: InventoryDragEvent) {
        withHolder(event.inventory.holder) {
            if (event.rawSlots.stream()
                    .anyMatch { rawSlot -> event.view.getInventory(rawSlot) != event.view.topInventory }
            ) {
                event.isCancelled = true
                return@withHolder
            }
            if (runtime.window == null) return@withHolder
            val valueHandler = ValueHandler()
            interactionHandler.onDrag(InventoryUIInteractionContext(runtime, event, valueHandler))

            if (!event.isCancelled) {
                runtime.viewportl.scafall.scheduler.syncTask(runtime.viewportl.scafall.modInfo) {
                    for (rawSlot in event.rawSlots) {
                        if (rawSlot < event.inventory.size) {
                            valueHandler.callSlotValueUpdate(rawSlot, event.inventory.getItem(rawSlot))
                        }
                    }
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onClose(event: InventoryCloseEvent) {
        withHolder(event.inventory.holder) {
            runtime.window?.apply { close() }
        }
    }
}

data class ValueHandler(
    val listeners: Multimap<Int, (ItemStack?) -> Unit> = Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }
) {

    fun callSlotValueUpdate(slot: Int, item: ItemStack?) {
        listeners[slot].forEach {
            it(item)
        }
    }

}

data class Slot(
    val index: Int
)