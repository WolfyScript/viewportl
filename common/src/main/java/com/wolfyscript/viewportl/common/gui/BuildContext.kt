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

package com.wolfyscript.viewportl.common.gui

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.reactivity.ReactiveGraph
import com.wolfyscript.viewportl.gui.components.NativeComponent
import java.util.*

class BuildContext(val runtime: ViewRuntimeImpl, val reactiveSource: ReactiveGraph, val viewportl: Viewportl) {

    val modelGraph = runtime.model

    private val ancestors: Deque<NativeComponent> = ArrayDeque()

    val currentParent: NativeComponent?
        get() = ancestors.peek()

    fun enterComponent(component: NativeComponent) {
        ancestors.push(component)
    }

    fun exitComponent() {
        ancestors.pop()
    }

    fun addComponent(component: NativeComponent): Long {
        val id = modelGraph.addNode(component)
        modelGraph.insertNodeAsChildOf(id, currentParent?.nodeId ?: 0)
        return id
    }

}
