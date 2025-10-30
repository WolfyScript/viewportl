package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.NodeArranger
import java.util.concurrent.atomic.AtomicLong

private val nodeIdGeneration = AtomicLong(0)
private fun generateNodeId(): Long = nodeIdGeneration.getAndIncrement()

class LayoutNode(val id: Long = generateNodeId()) : Node {

    override var parent: Node? = null
    private val children = mutableListOf<LayoutNode>()

    override var modifierStack = ModifierStackImpl(ArrayDeque())
    override var modifier: ModifierStackBuilder = ModifierStackBuilder {}
        set(value) {
            field = value
            val scope = ModifierStackScopeImpl()
            with(value) {
                scope.build()
            }
            modifierStack = scope.create()
        }

    override val arranger: NodeArranger = NodeArrangerImpl(this)
    override var measurePolicy: MeasurePolicy? = null

    override fun insertChildAt(index: Int, child: Node) {
        if (child !is LayoutNode) {
            return
        }
        child.parent = this
        children.add(index, child)

        child.attach()
    }

    override fun removeChildAt(index: Int, count: Int) {
        for (i in index + count - 1 downTo index) {
            onChildRemoved(children[i])
            children.removeAt(i)
        }
    }

    override fun moveChildren(from: Int, to: Int, count: Int) {
        if (from == to) {
            return
        }

        var fromIndex: Int = from
        var toIndex: Int = (to - 1) + (count - 1)
        for (i in 0 until count) {
            if (from > to) {
                fromIndex = from + i
                toIndex = to + i
            }
            val child = children.removeAt(fromIndex)
            children.add(toIndex, child)
        }
    }

    fun onChildRemoved(child: LayoutNode) {
        child.parent = null

        child.detach()
    }

    override fun clearChildren() {
        for (child in children) {
            onChildRemoved(child)
        }

        children.clear()
    }

    override fun forEachChild(action: (Node) -> Unit) {
        children.forEach(action)
    }

    private fun detach() {
        modifierStack.onNodeDetach()
    }

    private fun attach() {

        modifierStack.onNodeAttach()

    }

}