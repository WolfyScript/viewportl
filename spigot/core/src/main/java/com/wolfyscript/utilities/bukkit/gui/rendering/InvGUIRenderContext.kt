package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.rendering.RenderContext

class InvGUIRenderContext(val renderer: InventoryGUIRenderer) : RenderContext {
    private var currentNode: Component? = null
    private var slotOffsetToParent = 0

    fun setSlotOffset(offset: Int) {
        this.slotOffsetToParent = offset
    }

    override fun currentOffset(): Int {
        return slotOffsetToParent
    }

    override fun enterNode(component: Component) {
        this.currentNode = component
    }

    override fun exitNode() {
        this.currentNode = null
    }

    override fun getCurrentComponent(): Component? {
        return currentNode
    }
}
