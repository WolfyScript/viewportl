package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.UIInteractionHandler
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.DragInteractionDetails

class SpongeUIInteractionHandler(private val runtime: ViewRuntimeImpl) : UIInteractionHandler(runtime) {

    companion object {

        init {
            registerComponentInteractionHandler(ButtonImpl::class.java, InventoryButtonInteractionHandler())
            registerComponentInteractionHandler(SlotImpl::class.java, InventoryStackSlotInteractionHandler())
        }

    }

    override fun onClick(details: ClickInteractionDetails) {
        details as ClickInteractionDetailsImpl

        if (details.valid) {

            // Notify listeners (e.g. components)
//            details.callSlotValueUpdate(event.rawSlot, slotResult?.wrap())
        }
    }

    override fun onDrag(details: DragInteractionDetails) {
        val topInvSize = runtime.getCurrentMenu().map { it.size ?: 0 }.orElse(0)

    }

}