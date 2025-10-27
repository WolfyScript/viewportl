package com.wolfyscript.viewportl.common.gui.inventoryui.rendering

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.gui.rendering.Renderer

abstract class InvUIRenderer<T : InvUIRenderContext>(val contextType: Class<T>) : Renderer<T> {

    abstract fun createContext(): T

    abstract fun clearSlots(slots: Collection<Int>)

    /**
     * Renders the given [node]
     */
    override fun render(node: Node) {
        var startOffset = Offset.Zero
        if (node.parent != null) {
            var current: Node = node
            while (current.parent != null) {
                current = current.parent!!
                startOffset = Offset(startOffset.x + current.arranger.position.x, startOffset.y + current.arranger.position.y)
            }
        } else {
            startOffset = node.arranger.position // Root node usually 0,0
        }
        render(node, startOffset)
    }

    private fun render(node: Node, offset: Offset) {
        val drawMod = node.modifierStack.firstOfType(InventoryDrawModifierNode::class) // TODO: support multiple draw modifiers per Node
        val nodeOffset = Offset(
            x = offset.x + node.arranger.position.x,
            y = offset.y + node.arranger.position.y
        )
        if (drawMod != null) {
            val scope: InventoryDrawScope = object : InventoryDrawScope { // TODO: mutable shared scope instead to reduce allocations
                override val width: Int = nodeOffset.x.slot.value
                override val height: Int = nodeOffset.y.slot.value

                override fun drawStack(
                    offset: Offset,
                    stack: ItemStackSnapshot,
                ) {

                    TODO("Not yet implemented")
                }

            }

            with(drawMod) {
                scope.draw()
            }
        }

        node.forEachChild {
            render(it, nodeOffset)
        }
    }

}