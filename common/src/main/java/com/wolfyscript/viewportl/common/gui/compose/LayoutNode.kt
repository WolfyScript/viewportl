package com.wolfyscript.viewportl.common.gui.compose

import androidx.compose.runtime.ComposeNodeLifecycleCallback
import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.NodeArranger
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import java.util.concurrent.atomic.AtomicLong

private val nodeIdGeneration = AtomicLong(0)
private fun generateNodeId(): Long = nodeIdGeneration.getAndIncrement()

class LayoutNode(val id: Long = generateNodeId()) : Node, ComposeNodeLifecycleCallback {

    override var parent: Node? = null
    internal val children = mutableListOf<LayoutNode>()

    override val modifierStack = ModifierStackImpl()
    override var modifier: ModifierStackBuilder = Modifier
        set(value) {
            // Node reused. Modifiers were updated
            field = value
            modifierStack.update(value)
        }

    override val arranger: NodeArrangerImpl = NodeArrangerImpl(this)
    override var measurePolicy: MeasurePolicy? = null
        set(value) {
            // Node reused. MeasurePolicy was updated
            field = value

            // TODO: Invalidate measurements & request remeasure
        }

    var attached = false

    override fun insertChildAt(index: Int, child: Node) {
        if (child !is LayoutNode) {
            return
        }
        child.parent = this
        children.add(index, child)

        child.attach()
    }

    override fun removeChildrenAt(index: Int, count: Int) {
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

    /**
     * Node was detached from the compose graph.
     */
    private fun detach() {
        attached = false

        modifierStack.onNodeDetach()
        children.forEach(LayoutNode::detach)
    }

    /**
     * Node was attached to the compose graph.
     */
    private fun attach() {
        modifierStack.onNodeAttach()

        attached = true
    }

    override fun onReuse() {
    }

    override fun onDeactivate() {
    }

    override fun onRelease() {
    }



}