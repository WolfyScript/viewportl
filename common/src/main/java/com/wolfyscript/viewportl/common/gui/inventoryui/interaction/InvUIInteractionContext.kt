package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler

class InvUIInteractionContext (val interactionHandler: InteractionHandler<*>) {
    private var currentNode: NativeComponent? = null
    private var slotOffsetToParent = 0

    fun setSlotOffset(offset: Int) {
        this.slotOffsetToParent = offset
    }

    fun currentOffset(): Int {
        return slotOffsetToParent
    }

    fun enterNode(nativeComponent: NativeComponent) {
        this.currentNode = nativeComponent
    }

    fun exitNode() {
        this.currentNode = null
    }

    fun getCurrentComponent(): NativeComponent? {
        return currentNode
    }
}