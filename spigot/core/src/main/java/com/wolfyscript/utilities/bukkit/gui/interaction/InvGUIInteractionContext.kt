package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.bukkit.gui.rendering.InventoryGUIRenderer
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.interaction.InteractionHandler
import com.wolfyscript.utilities.gui.rendering.RenderContext

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