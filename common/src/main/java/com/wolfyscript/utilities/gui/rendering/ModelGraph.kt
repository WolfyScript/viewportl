/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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

package com.wolfyscript.utilities.gui.rendering

import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import com.wolfyscript.utilities.gui.components.Component
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.components.AbstractComponentImpl
import com.wolfyscript.utilities.gui.model.UpdateInformation
import java.util.Collections

class ModelGraph(private val runtime: ViewRuntimeImpl) {

    private var nodeCount: Long = 0
    private val nodes: MutableMap<Long, Node> = mutableMapOf()
    private val children: SetMultimap<Long, Long> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val parents: MutableMap<Long, Long> = mutableMapOf()

    fun addNode(component: Component) : Long {
        val id = ++nodeCount
        nodes[id] = Node(id, component)
        if (component is AbstractComponentImpl<*>) {
            component.nodeId = id
        }
        return id
    }

    fun insertComponentAt(component: Component, insertAt: Long) {
        if (insertAt != 0L && !nodes.containsKey(insertAt)) return
        val id = addNode(component)
        insertNodeAsChildOf(id, insertAt)
    }

    fun insertNodeAsChildOf(nodeId: Long, parent: Long) {
        if(!nodes.containsKey(nodeId) || (!nodes.containsKey(parent) && parent != 0L)) return
        val siblings = children[parent]
        val previousSibling = siblings.lastOrNull()
        siblings.add(nodeId)
        parents[nodeId] = parent

        runtime.incomingUpdate(object : UpdateInformation{

            override fun added(): List<Pair<Long?, Long>> {
                return listOf(Pair(previousSibling, nodeId))
            }
        })
    }

    fun getNode(id: Long) : Node? {
        return nodes[id]
    }

    fun children(id: Long) : Set<Long> {
        return Collections.unmodifiableSet(children[id])
    }

    fun parent(id: Long) : Long? {
        return parents[id]
    }

    fun removeNode(nodeId: Long) {
        runtime.incomingUpdate(object : UpdateInformation{

            override fun removed(): List<Long> {
                return listOf(nodeId)
            }

        })

        nodes.remove(nodeId)
        val parent = parents.remove(nodeId)
        if (parent != null) {
            children[parent].remove(nodeId)
        }

        // Recursively remove child nodes
        val removedChildren = children.removeAll(nodeId)
        for (child in removedChildren) {
            removeNode(child)
        }
    }
}