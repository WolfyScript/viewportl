package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.compose.modifier.PointerEventScopeImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.modifier.ClickableModifierNode

abstract class InvUIInteractionHandler<C: InteractionContext> : InteractionHandler<C> {

    lateinit var runtime: ViewRuntime
    val slotNodes: MutableMap<Int, Node> = mutableMapOf()
    val cachedProperties: MutableMap<Long, CachedNodeInteractProperties> = mutableMapOf()

    override fun init(runtime: ViewRuntime) {
        this.runtime = runtime
    }

    override fun dispose() {

    }

    override fun onWindowOpen(window: Window) {
        val context = InvUIInteractionContext(this)
        cachedProperties[0] = CachedNodeInteractProperties(0, mutableListOf(0))
        context.setSlotOffset(0)

    }

    protected fun onClick(slot: Int, root: Node) {
        val y = slot / 9
        val x = slot - y * 9

        var currentNode = root
        var totalOffset: Offset = root.arranger.position

        val scope = PointerEventScopeImpl()
        var processing = true
        while (processing) {
            var intersects = false
            currentNode.forEachChild { node ->
                if (withinBounds(x, y, node, totalOffset)) {
                    currentNode = node
                    totalOffset += currentNode.arranger.position
                    intersects = true

                    node.modifierStack.firstOfType(ClickableModifierNode::class)?.let {
                        with(it) {
                            scope.onClickInteraction(x, y)
                        }
                    }
                }
            }
            if (!intersects) {
                processing = false
            }
        }
    }

    protected fun withinBounds(x: Int, y: Int, node: Node, parentOffset: Offset): Boolean {
        val posX = node.arranger.position.x.slot.value + parentOffset.x.slot.value
        val posY = node.arranger.position.y.slot.value + parentOffset.y.slot.value

        return x >= posX && x < posX + node.arranger.width.slot.value &&
            y >= posY && y < posY + node.arranger.height.slot.value
    }

}