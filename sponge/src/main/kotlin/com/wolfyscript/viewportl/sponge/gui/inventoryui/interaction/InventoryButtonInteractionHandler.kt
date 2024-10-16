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

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.interaction.*
import org.spongepowered.api.data.Keys
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.transaction.SlotTransaction

class InventoryButtonInteractionHandler : SpongeComponentInteractionHandler<Button> {

    private fun playSound(runtime: ViewRuntime<*, *>, component: Button) {
        component.sound?.let { sound ->
            runtime.viewers.forEach {
                runtime.viewportl.scafall.adventure.player(it).playSound(sound)
            }
        }
    }

    override fun onSingleSlotClick(
        runtime: ViewRuntime<*, SpongeUIInteractionHandler>,
        component: Button,
        event: ClickContainerEvent
    ) {
        event.transactions().firstOrNull()?.let { transaction ->
            handleClick(runtime, component, event, transaction)
        }

        event.cursorTransaction().isValid = false
        event.transactions().forEach { it.isValid = false }
    }

    override fun onDrag(
        runtime: ViewRuntime<*, SpongeUIInteractionHandler>,
        component: Button,
        slotTransaction: SlotTransaction,
        event: ClickContainerEvent.Drag
    ) {
        if (!slotTransaction.isValid) {
            return
        }

        handleClick(runtime, component, event, slotTransaction)

        event.cursorTransaction().isValid = false
        slotTransaction.isValid = false
    }

    private fun handleClick(runtime: ViewRuntime<*,SpongeUIInteractionHandler>, component: Button, event: ClickContainerEvent, transaction: SlotTransaction) {
        playSound(runtime, component)

        component.onClick?.let { click ->
            event.convert(transaction).click()
        }
    }

}