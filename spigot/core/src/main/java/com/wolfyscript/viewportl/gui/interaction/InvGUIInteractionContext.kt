package com.wolfyscript.viewportl.gui.interaction

import com.wolfyscript.viewportl.gui.components.Component
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler

class InvGUIInteractionContext (val interactionHandler: InteractionHandler) {
    private var currentNode: Component? = null
    private var slotOffsetToParent = 0

    fun setSlotOffset(offset: Int) {
        this.slotOffsetToParent = offset
    }

    fun currentOffset(): Int {
        return slotOffsetToParent
    }

    fun enterNode(component: Component) {
        this.currentNode = component
    }

    fun exitNode() {
        this.currentNode = null
    }

    fun getCurrentComponent(): Component? {
        return currentNode
    }
}