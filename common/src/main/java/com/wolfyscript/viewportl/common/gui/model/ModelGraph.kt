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

    override fun addNode(element: Element) : Long {
        return 0
    }

    override fun insertNodeAsChildOf(nodeId: Long, parent: Long) {
    }

    override fun getNode(id: Long) : Node? {
        return null
    }

    override fun children(id: Long) : Set<Long> {
        return Collections.emptySet()
    }

    override fun parent(id: Long) : Long? {
        return null
    }

    override fun clearNodeChildren(nodeId: Long) {

    }

}
