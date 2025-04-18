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

import com.wolfyscript.viewportl.gui.elements.Element

/**
 * An acyclic Graph (Tree) of [Elements][Element]
 *
 * This model can be manipulated by adding or removing nodes ([Element]).
 * Those changes are then sent to the listeners (e.g. renderer and interaction handler).
 *
 */
interface ModelGraph {

    fun registerListener(listener: ModelChangeListener)

    fun unregisterListener(listener: ModelChangeListener)

    fun addNode(element: Element) : Long

    fun insertComponentAt(element: Element, insertAt: Long)

    fun insertNodeAsChildOf(nodeId: Long, parent: Long)

    fun getNode(id: Long) : Node?

    fun children(id: Long) : Set<Long>

    fun parent(id: Long) : Long?

    fun clearNodeChildren(nodeId: Long)

    fun removeNode(nodeId: Long)

    fun updateNode(nodeId: Long)

}

/**
 * Represents a Node in the [ModelGraph]. It consists of the associated id and [Element]
 */
class Node(val id: Long, val element: Element)

/**
 * Listens to updates of the [ModelGraph]
 *
 * For it to work any class extending this interface needs to be registered via [ModelGraph.registerListener]
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
    val node: Node
)

data class NodeRemovedEvent(
    val node: Node
)

data class NodeAddedEvent(
    val node: Node
)
