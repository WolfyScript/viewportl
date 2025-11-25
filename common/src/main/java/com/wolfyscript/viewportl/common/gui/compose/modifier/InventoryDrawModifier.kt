package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawModifierData
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope

class InventoryDrawModifierDataImpl(
    override val draw: InventoryDrawScope.() -> Unit
) : InventoryDrawModifierData<InventoryDrawModifierNodeImpl> {

    override fun create(): InventoryDrawModifierNodeImpl {
        return InventoryDrawModifierNodeImpl(draw)
    }

    override fun update(node: InventoryDrawModifierNodeImpl) {
        node.onDraw = draw
    }

}


class InventoryDrawModifierNodeImpl(
    internal var onDraw: InventoryDrawScope.() -> Unit
) : InventoryDrawModifierNode {

    private var previousScope: InventoryDrawScope? = null

    override fun InventoryDrawScope.draw() {
        onDraw()
        previousScope = this
    }

    override fun onMeasurementsChanged() {
        if (previousScope != null) {
            previousScope!!.clear()
        }
    }

    override fun onLayoutChanged() {
        if (previousScope != null) {
            previousScope!!.clear()
        }
    }

    override fun onAttach() {

    }

    override fun onDetach() {
        if (previousScope != null) {
            previousScope!!.clear()
        }
    }

}