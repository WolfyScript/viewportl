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
import com.wolfyscript.utilities.gui.Component
import java.util.Collections

class RenderingGraph {

    private var nodeCount: Long = 0
    private val nodes: MutableMap<Long, RenderingNode> = mutableMapOf()
    private val children: SetMultimap<Long, Long> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf<Long>() }
    private val parents: MutableMap<Long, Long> = mutableMapOf()

    fun addNode(component: Component) : Long {
        val id = ++nodeCount
        nodes[id] = RenderingNode(id, component)
        return id
    }

    fun insertComponentAt(component: Component, insertAt: Long) {
        if (!nodes.containsKey(insertAt)) return
        val id = addNode(component)
        insertNodeChild(id, insertAt)
    }

    fun insertNodeChild(nodeId: Long, parent: Long) {
        if(!nodes.containsKey(nodeId) || !nodes.containsKey(parent)) return
        children[parent].add(nodeId)
        parents[nodeId] = parent
    }

    fun getNode(id: Long) : RenderingNode? {
        return nodes[id]
    }

    fun children(id: Long) : Set<Long> {
        return Collections.unmodifiableSet(children[id])
    }

    fun removeNode(nodeId: Long) {
        nodes.remove(nodeId)
        parents.remove(nodeId)

        // Recursively remove child nodes
        for (child in children[nodeId]) {
            removeNode(child)
        }
        children.removeAll(nodeId)
    }
}