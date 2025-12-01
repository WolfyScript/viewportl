package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.compose.modifier.PointerEventScopeImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.modifier.ClickableModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.SlotInputModifierNode

abstract class InvUIInteractionHandler<C: InteractionContext> : InteractionHandler<C> {

    lateinit var runtime: ViewRuntime
    val slotNodes: MutableMap<Int, Node> = mutableMapOf()

    override fun init(runtime: ViewRuntime) {
        this.runtime = runtime
    }

    override fun dispose() {

    }

    override fun onWindowOpen(window: Window) {

    }

    protected fun getSlotInputFor(slot: Int, root: Node): SlotInputModifierNode? {
        val y = slot / 9
        val x = slot - y * 9

        var currentNode = root
        var totalOffset: Offset = root.arranger.position
        var modifier: SlotInputModifierNode? = null

        var processing = true
        while (processing) {
            var intersects = false
            currentNode.forEachChild { node ->
                if (withinBounds(x, y, node, totalOffset)) {
                    currentNode = node
                    totalOffset += currentNode.arranger.position
                    intersects = true
                    modifier = node.modifierStack.firstOfType(SlotInputModifierNode::class)
                    if (modifier != null) {
                        processing = false
                        return@forEachChild
                    }
                }
            }
            if (!intersects) {
                processing = false
            }
        }
        return modifier
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
        val posX = (node.arranger.position.x + parentOffset.x).roundToSlots()
        val posY = (node.arranger.position.y + parentOffset.y).roundToSlots()

        return x >= posX && x < posX + node.arranger.width.roundToSlots() &&
            y >= posY && y < posY + node.arranger.height.roundToSlots()
    }

}