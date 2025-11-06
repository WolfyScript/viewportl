package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawModifierData
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope

class InventoryDrawModifierDataImpl(
    override val draw: InventoryDrawScope.() -> Unit
) : InventoryDrawModifierData {

    override fun create(): InventoryDrawModifierNode {
        return InventoryDrawModifierNodeImpl(draw)
    }

    override fun update(node: InventoryDrawModifierNode) {

    }

}


class InventoryDrawModifierNodeImpl(
    val onDraw: InventoryDrawScope.() -> Unit
) : InventoryDrawModifierNode {

    override fun InventoryDrawScope.draw() {
        onDraw()
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}