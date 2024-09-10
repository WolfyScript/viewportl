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
package com.wolfyscript.viewportl.spigot.gui.inventoryui.interaction

import com.wolfyscript.scafall.spigot.api.wrappers.wrap
import com.wolfyscript.viewportl.common.gui.reactivity.ReactiveGraph
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.spigot.gui.inventoryui.BukkitInventoryGuiHolder
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class InventoryUIListener(val runtime: ViewRuntime<*, SpigotInvUIInteractionHandler>) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInvClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        if (holder is BukkitInventoryGuiHolder) {
//            bukkitInventoryGuiHolder.guiHolder().getViewManager().blockedByInteraction();
            val details = ClickInteractionDetailsImpl(event)
            runtime.interactionHandler.onClick(details)
            event.isCancelled = true

            runtime.viewportl.scafall.scheduler.syncTask(runtime.viewportl.scafall.corePlugin) {
                (runtime.reactiveSource as ReactiveGraph).runEffects()
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onItemDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is BukkitInventoryGuiHolder) {
//            bukkitInventoryGuiHolder.guiHolder().getViewManager().blockedByInteraction();
            if (event.rawSlots.stream()
                    .anyMatch { rawSlot -> event.view.getInventory(rawSlot) != event.view.topInventory }
            ) {
                event.isCancelled = true
                return
            }
            if (runtime.window == null) return
            val interactionDetails = DragInteractionDetailsImpl(event)
            runtime.interactionHandler.onDrag(interactionDetails)
            if (!interactionDetails.valid) {
                event.isCancelled = true
            } else {
                runtime.viewportl.scafall.scheduler.syncTask(runtime.viewportl.scafall.corePlugin) {
                    for (rawSlot in event.rawSlots) {
                        if (rawSlot < event.inventory.size) {
                            interactionDetails.callSlotValueUpdate(rawSlot, event.inventory.getItem(rawSlot)?.wrap())
                        }
                    }
                }
            }

            runtime.viewportl.scafall.scheduler.syncTask(runtime.viewportl.scafall.corePlugin) {
                (runtime.reactiveSource as ReactiveGraph).runEffects()
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder is BukkitInventoryGuiHolder) {
//            bukkitInventoryGuiHolder.guiHolder().getViewManager().blockedByInteraction();
            runtime.window?.apply { close() }
        }
    }
}
