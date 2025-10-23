package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.ModifierStack
import com.wolfyscript.viewportl.gui.compose.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Arranger
import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.compose.layout.PlacementScope
import com.wolfyscript.viewportl.gui.compose.layout.Size
import java.util.concurrent.atomic.AtomicLong

private val nodeIdGeneration = AtomicLong(0)
private fun generateNodeId(): Long = nodeIdGeneration.getAndIncrement()

class LayoutNode(val id: Long = generateNodeId()) : Node {

    override var parent: Node? = null
    private val children = mutableListOf<Node>()

    private var modifierStack: ModifierStack = ModifierStackImpl(ArrayDeque())
    override var modifier: ModifierStackBuilder = ModifierStackBuilder {}
        set(value) {
            field = value
            val scope = ModifierStackScopeImpl()
            with(value) {
                scope.build()
            }
            modifierStack = scope.create()
        }

    override val arranger: Arranger = ArrangerImpl(this)
    override var measurePolicy: MeasurePolicy? = null
    private var measurements: Measurements? = null

    override fun insertChildAt(index: Int, child: Node) {
        child.parent = this
        children.add(index, child)
    }

    override fun removeChildAt(index: Int, count: Int) {
        for (i in index + count - 1 downTo index) {
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

    override fun clearChildren() {
        children.clear()
    }

    override fun forEachChild(action: (Node) -> Unit) {
        children.forEach(action)
    }

    override fun measureAndLayout(constraints: Constraints): Measurements {
        // TODO: Measure own size constraints

        val scope = SimpleMeasureScope()

        for (child in children) {
            // TODO: Use own constraints to measure children
            child.measureAndLayout(constraints)
        }

        // TODO: Place Children

        // TODO: Return info to parent
        return measurements!!
    }

}