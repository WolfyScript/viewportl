package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler

class InvUIInteractionContext (val interactionHandler: InteractionHandler<*>) {
    private var currentNode: Element? = null
    private var slotOffsetToParent = 0

    fun setSlotOffset(offset: Int) {
        this.slotOffsetToParent = offset
    }

    fun currentOffset(): Int {
        return slotOffsetToParent
    }

    fun enterNode(element: Element) {
        this.currentNode = element
    }

    fun exitNode() {
        this.currentNode = null
    }

    fun getCurrentComponent(): Element? {
        return currentNode
    }
}