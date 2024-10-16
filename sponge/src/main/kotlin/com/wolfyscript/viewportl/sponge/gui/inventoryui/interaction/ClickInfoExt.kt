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

package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.scafall.sponge.api.wrappers.wrap
import com.wolfyscript.viewportl.common.gui.interaction.CommonClickInfoImpl
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import org.spongepowered.api.data.Keys
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.Slot
import org.spongepowered.api.item.inventory.transaction.SlotTransaction

fun ClickContainerEvent.convert(transaction: SlotTransaction): ClickInfo {
    val slot: Slot = transaction.slot()
    val index = slot.get(Keys.SLOT_INDEX).orElse(null) ?: throw IndexOutOfBoundsException("Failed to convert ClickContainer event. Could not find index: $slot")

    val slotStack = transaction.original().createStack().wrap()
    val cursorStack = cursorTransaction().original().createStack().wrap()

    return when(this) {
        is ClickContainerEvent.Drop -> {
            when (this) {
                is ClickContainerEvent.Drop.Full -> CommonClickInfoImpl.Drop.Full(index, cursorStack, slotStack)
                is ClickContainerEvent.Drop.Single -> CommonClickInfoImpl.Drop.Single(index, cursorStack, slotStack)
                is ClickContainerEvent.Drop.Outside.Primary -> CommonClickInfoImpl.Drop.Outside.Primary(index, cursorStack, slotStack)
                is ClickContainerEvent.Drop.Outside.Secondary -> CommonClickInfoImpl.Drop.Outside.Secondary(index, cursorStack, slotStack)
                else -> throw Exception("Failed to convert ClickContainerEvent.Drop. Unknown click type: $this")
            }
        }

        is ClickContainerEvent.Drag -> {
            when(this) {
                is ClickContainerEvent.Drag.Primary -> CommonClickInfoImpl.Drag.Primary(index, cursorStack, slotStack)
                is ClickContainerEvent.Drag.Secondary -> CommonClickInfoImpl.Drag.Secondary(index, cursorStack, slotStack)
                is ClickContainerEvent.Drag.Middle -> CommonClickInfoImpl.Drag.Middle(index, cursorStack, slotStack)
                else -> throw Exception("Failed to convert ClickContainerEvent.Drag. Unknown drag type: $this")
            }
        }

        is ClickContainerEvent.Shift.Primary -> CommonClickInfoImpl.Shift.Primary(index, cursorStack, slotStack)
        is ClickContainerEvent.Shift.Secondary -> CommonClickInfoImpl.Shift.Secondary(index, cursorStack, slotStack)

        is ClickContainerEvent.Double -> CommonClickInfoImpl.Double(index, cursorStack, slotStack)
        is ClickContainerEvent.Primary -> CommonClickInfoImpl.Primary(index, cursorStack, slotStack)
        is ClickContainerEvent.Secondary -> CommonClickInfoImpl.Secondary(index, cursorStack, slotStack)
        is ClickContainerEvent.Middle -> CommonClickInfoImpl.Middle(index, cursorStack, slotStack)

        is ClickContainerEvent.NumberPress -> CommonClickInfoImpl.NumberPress(index, number(), cursorStack, slotStack)

        else -> throw Exception("Failed to convert ClickContainerEvent. Unknown click type: $this")
    }

}