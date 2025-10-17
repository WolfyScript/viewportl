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

package com.wolfyscript.viewportl.common.gui.model

import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import com.wolfyscript.viewportl.common.gui.elements.AbstractElementImpl
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.model.*
import java.util.*

/**
 * An acyclic Graph (Tree) of [NativeComponents][Element]
 *
 * This model can be manipulated by adding or removing nodes ([Element]).
 * Those changes are then sent to all registered listeners.
 *
 */
class ModelGraphImpl : ModelGraph {

    private var nodeCount: Long = 0
    private val nodes: MutableMap<Long, Node> = mutableMapOf()
    private val children: SetMultimap<Long, Long> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val parents: MutableMap<Long, Long> = mutableMapOf()
    private val listeners: MutableSet<ModelChangeListener> = mutableSetOf()

    override fun registerListener(listener: ModelChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: ModelChangeListener) {
        listeners.remove(listener)
    }

    override fun addNode(element: Element) : Long {
        val id = ++nodeCount
        nodes[id] = Node(id, element)
        if (element is AbstractElementImpl<*>) {
            element.currentNodeId = id
        }
        return id
    }

    override fun insertComponentAt(element: Element, insertAt: Long) {
        if (insertAt != 0L && !nodes.containsKey(insertAt)) return
        val id = addNode(element)
        insertNodeAsChildOf(id, insertAt)
    }

    override fun insertNodeAsChildOf(nodeId: Long, parent: Long) {
        if(!nodes.containsKey(nodeId) || (!nodes.containsKey(parent) && parent != 0L)) return
        val siblings = children[parent]
        siblings.add(nodeId)
        parents[nodeId] = parent

        listeners.forEach { it.onNodeAdded(NodeAddedEvent(this, nodes[nodeId]!!)) }
    }

    override fun getNode(id: Long) : Node? {
        return nodes[id]
    }

    override fun children(id: Long) : Set<Long> {
        return Collections.unmodifiableSet(children[id])
    }

    override fun parent(id: Long) : Long? {
        return parents[id]
    }

    override fun clearNodeChildren(nodeId: Long) {
        // Recursively remove child nodes
        val removedChildren = children.removeAll(nodeId)
        for (child in removedChildren) {
            removeNode(child)
        }
    }

    override fun removeNode(nodeId: Long) {
        val node = nodes.remove(nodeId) ?: return // If no node was removed we can just skip it
        listeners.forEach { it.onNodeRemoved(NodeRemovedEvent(this, node)) }

        // Remove this node from the parent children list
        val parent = parents.remove(nodeId)
        if (parent != null) {
            children[parent].remove(nodeId)
        }
        clearNodeChildren(nodeId)
    }

    override fun updateNode(nodeId: Long) {
        val node = nodes[nodeId] ?: return // If no node was removed we can just skip it
        listeners.forEach { it.onNodeUpdated(NodeUpdatedEvent(this, node)) }
    }
}
