package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.platform.adapters.ItemStack
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