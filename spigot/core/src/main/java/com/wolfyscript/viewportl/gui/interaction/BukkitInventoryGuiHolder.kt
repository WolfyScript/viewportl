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

package com.wolfyscript.viewportl.gui.interaction

import com.wolfyscript.scafall.spigot.api.wrappers.world.items.ItemStackImpl
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.Window
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

internal class BukkitInventoryGuiHolder(private val runtime: ViewRuntimeImpl, private val guiHolder: GuiHolder) :
    InventoryHolder {
    private var activeInventory: Inventory? = null

    private fun currentWindow(): Window {
        return guiHolder.currentWindow
    }

    fun guiHolder(): GuiHolder {
        return guiHolder
    }

    fun onClick(event: InventoryClickEvent) {
        if (currentWindow() == null || event.clickedInventory == null) return
        if (event.view.topInventory.holder == this) {
            val details = com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetailsImpl(event)
            runtime.interactionHandler.onClick(details)
            event.isCancelled = true

            runtime.scaffolding.scheduler.syncTask(runtime.scaffolding.corePlugin) {
                runtime.reactiveSource.runEffects()
            }
        }
    }

    fun onDrag(event: InventoryDragEvent) {
        if (event.rawSlots.stream().anyMatch { rawSlot: Int? ->
                event.view.getInventory(rawSlot!!) != activeInventory
            }) {
            event.isCancelled = true
            return
        }
        if (currentWindow() == null) return
        if (event.inventory.holder == this) {
            val interactionDetails = DragInteractionDetailsImpl(runtime.wolfyUtils, event)
            runtime.interactionHandler.onDrag(interactionDetails)
            if (!interactionDetails.valid) {
                event.isCancelled = true
            } else {
                runtime.scaffolding.scheduler.syncTask(runtime.scaffolding.corePlugin) {
                    for (rawSlot in event.rawSlots) {
                        if (rawSlot < event.inventory.size) {
                            interactionDetails.callSlotValueUpdate(rawSlot, ItemStackImpl(event.inventory.getItem(rawSlot)))
                        }
                    }
                }
            }

            runtime.scaffolding.scheduler.syncTask(runtime.scaffolding.corePlugin) {
                runtime.reactiveSource.runEffects()
            }
        }
    }

    fun onClose(event: InventoryCloseEvent) {
        // TODO: Close Window
        if (currentWindow() == null) return
        if (event.inventory.holder == this) {
            guiHolder.viewManager.currentMenu?.apply { close() }
        }
    }

    fun setActiveInventory(activeInventory: Inventory?) {
        this.activeInventory = activeInventory
    }

    override fun getInventory(): Inventory {
        return activeInventory!!
    }
}
