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

import com.wolfyscript.scafall.sponge.api.wrappers.world.items.ItemStackWrapper
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.interaction.*
import org.spongepowered.api.data.Keys
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.transaction.SlotTransaction

class InventoryStackSlotInteractionHandler : SpongeComponentInteractionHandler<StackInputSlot> {

    override fun onSingleSlotClick(
        runtime: ViewRuntime<*, SpongeUIInteractionHandler>,
        component: StackInputSlot,
        event: ClickContainerEvent
    ) {
        for (transaction in event.transactions()) {
            handleTransaction(component, transaction)
        }
    }

    override fun onDrag(
        runtime: ViewRuntime<*, SpongeUIInteractionHandler>,
        component: StackInputSlot,
        slotTransaction: SlotTransaction,
        event: ClickContainerEvent.Drag
    ) {
        handleTransaction(component, slotTransaction)
    }

    private fun handleTransaction(component: StackInputSlot, transaction: SlotTransaction) {
        if (!transaction.isValid) {
            return
        }
        val slot = transaction.slot()
        slot.get(Keys.SLOT_INDEX).ifPresent { slotIndex ->
            // TODO: Check if stack can be accepted (invalidate transaction otherwise)

            val wrappedFinalReplacement = ItemStackWrapper(transaction.finalReplacement().createStack())  // TODO: Wrap stack snapshot instead?
            component.value = wrappedFinalReplacement
            component.onValueChange?.accept(wrappedFinalReplacement)
        }
    }

    override fun onDrag(
        runtime: ViewRuntime<*, *>,
        component: StackInputSlot,
        details: DragInteractionDetails,
        transaction: DragTransaction
    ) {}

    override fun onClick(
        runtime: ViewRuntime<*, *>,
        component: StackInputSlot,
        details: ClickInteractionDetails,
        transaction: ClickTransaction
    ) {}
}