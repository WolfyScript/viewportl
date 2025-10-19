/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.viewportl.gui.model

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.elements.Element

/**
 * An acyclic Graph (Tree) of [Elements][Element]
 *
 * This model can be manipulated by adding or removing nodes ([Element]).
 * Those changes are then sent to the listeners (e.g. renderer and interaction handler).
 *
 */
interface ModelGraph {

    fun addNode(element: Element) : Long

    fun insertNodeAsChildOf(nodeId: Long, parent: Long)

    fun getNode(id: Long) : Node?

    fun children(id: Long) : Set<Long>

    fun parent(id: Long) : Long?

    fun clearNodeChildren(nodeId: Long)

}

private val nodeIdGeneration = java.util.concurrent.atomic.AtomicLong(0)
private fun generateNodeId(): Long = nodeIdGeneration.getAndIncrement()

/**
 * Represents a Node in the Graph. It consists of the associated id and [Element]
 */
class Node(val id: Long = generateNodeId(), val element: Element? = null) {

    private var parent: Node? = null
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

/**
 * Listens to updates of the [ModelGraph]
 *
 */
interface ModelChangeListener {

    /**
     * Called when a node was added to the graph.
     *
     * Note: This will be called for each child node that may be added.
     */
    fun onNodeAdded(event: NodeAddedEvent)

    /**
     * Called when a node was removed from the graph.
     *
     * Note: This will be called for each child node that may be removed too.
     */
    fun onNodeRemoved(event: NodeRemovedEvent)

    /**
     * Called when a node in the graph was updated, meaning a property of the node was changed.
     */
    fun onNodeUpdated(event: NodeUpdatedEvent)

}

data class NodeUpdatedEvent(
    val model: ModelGraph,
    val node: Node
)

data class NodeRemovedEvent(
    val model: ModelGraph,
    val node: Node
)

data class NodeAddedEvent(
    val model: ModelGraph,
    val node: Node
)
