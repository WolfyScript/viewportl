package com.wolfyscript.viewportl.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.elements.Element
import java.util.concurrent.atomic.AtomicLong

private val nodeIdGeneration = AtomicLong(0)
private fun generateNodeId(): Long = nodeIdGeneration.getAndIncrement()

/**
 * Represents a Node in the Graph. It consists of the associated id and [Element]
 */
class Node(val id: Long = generateNodeId(), val element: Element? = null) {

    var parent: Node? = null
    private val children = mutableListOf<Node>()

    private var measurements: Measurements? = null

    fun insertChildAt(index: Int, child: Node) {
        child.parent = this
        children.add(index, child)
    }

    fun removeChildAt(index: Int, count: Int) {
        for (i in index + count - 1 downTo index) {
            children.removeAt(i)
        }
    }

    fun moveChildren(from: Int, to: Int, count: Int) {
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

    fun clearChildren() {
        children.clear()
    }

    fun forEachChild(action: (Node) -> Unit) {
        children.forEach(action)
    }

    fun remeasure() {

    }

    fun measureAndLayout(constraints: Constraints): Measurements {
        // TODO: Measure own size constraints

        for (child in children) {
            // TODO: Use own constraints to measure children
            child.measureAndLayout(constraints)
        }

        // TODO: Place Children

        // TODO: Return info to parent
        return measurements!!
    }

}