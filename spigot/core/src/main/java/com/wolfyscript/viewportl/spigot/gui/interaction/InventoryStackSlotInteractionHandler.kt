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

package com.wolfyscript.viewportl.spigot.gui.interaction

import com.wolfyscript.viewportl.common.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.interaction.*

class InventoryStackSlotInteractionHandler : ComponentInteractionHandler<StackInputSlot> {

    override fun onDrag(
        runtime: ViewRuntime,
        component: StackInputSlot,
        details: DragInteractionDetails,
        transaction: DragTransaction
    ) {
        component.onDrag?.let {
            with(it) {
                transaction.consume()
            }
        }

        details.onSlotValueUpdate(transaction.slot) {
            component.onValueChange?.accept(it)
            component.value = it
        }
    }

    override fun onClick(
        runtime: ViewRuntime,
        component: StackInputSlot,
        details: ClickInteractionDetails,
        transaction: ClickTransaction
    ) {
        transaction.validate() // Validate stack input by default

        component.onClick?.let {
            with(it) {
                transaction.consume()
            }
        }

        if (transaction.valid) {
            details.onSlotValueUpdate(transaction.rawSlot) {
                component.onValueChange?.accept(it)
                component.value = it
            }
        }
    }
}