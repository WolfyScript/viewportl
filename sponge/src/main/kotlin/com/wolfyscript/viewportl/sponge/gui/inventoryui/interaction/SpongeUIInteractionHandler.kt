package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.InvUIInteractionHandler
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.DragInteractionDetails

class SpongeUIInteractionHandler : InvUIInteractionHandler<SpongeUIInteractionHandler>() {

    companion object {

        init {
            registerComponentInteractionHandler(ButtonImpl::class.java, InventoryButtonInteractionHandler())
            registerComponentInteractionHandler(SlotImpl::class.java, InventoryStackSlotInteractionHandler())
        }

    }

    fun onClick(details: ClickInteractionDetails) {
        details as ClickInteractionDetailsImpl

        if (details.valid) {

            // Notify listeners (e.g. components)
//            details.callSlotValueUpdate(event.rawSlot, slotResult?.wrap())
        }
    }

    fun onDrag(details: DragInteractionDetails) {
        val topInvSize = runtime.window?.size ?: 0

    }

}