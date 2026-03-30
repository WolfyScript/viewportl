package com.wolfyscript.viewportl.fabric.server.ui.inventory

import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.runtime.UIRuntime
import com.wolfyscript.viewportl.runtime.View
import com.wolfyscript.viewportl.ui.modifier.SlotInputModifierNode
import java.util.UUID

class FabricInvUIInteractionHandler() : InteractionHandler<InventoryUIInteractionContext> {

    override var isBusy: Boolean = false

    override fun init(runtime: UIRuntime) {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun onViewOpened(view: View) {
        TODO("Not yet implemented")
    }

    override fun onClick(context: InventoryUIInteractionContext) {
        TODO("Not yet implemented")
    }

    override fun onDrag(context: InventoryUIInteractionContext) {
        TODO("Not yet implemented")
    }

    override fun findSlotInputAt(
        viewer: UUID,
        slotIndex: Int,
    ): SlotInputModifierNode? {
        TODO("Not yet implemented")
    }

    override fun clicked(viewer: UUID, slotIndex: Int) {
        TODO("Not yet implemented")
    }
}