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
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

class InventoryStackSlotInteractionHandler : SpigotComponentInteractionHandler<StackInputSlot> {

    override fun onDrag(
        runtime: ViewRuntime<*, SpigotInvUIInteractionHandler>,
        component: StackInputSlot,
        event: InventoryDragEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {
        valueHandler.listeners.put(slot.index) {
            val wrapped = it?.wrap()
            component.onValueChange?.accept(wrapped)
            component.value = wrapped
        }
    }

    override fun onClick(
        runtime: ViewRuntime<*, SpigotInvUIInteractionHandler>,
        component: StackInputSlot,
        event: InventoryClickEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {
        valueHandler.listeners.put(event.rawSlot) {
            val wrapped = it?.wrap()
            component.onValueChange?.accept(wrapped)
            component.value = wrapped
        }
    }

}