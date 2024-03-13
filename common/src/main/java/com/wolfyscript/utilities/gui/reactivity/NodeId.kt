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

package com.wolfyscript.utilities.gui.reactivity

import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import java.util.function.Function

class NodeId(val id: Long, val runtime: ViewRuntimeImpl) {

    fun <V> update(updateFn: Function<V, V>) {
        val reactivityNode: ReactivityNode<V>? = runtime.reactiveSource.node(this)
        if (reactivityNode != null) {
            reactivityNode.value?.let { updateFn.apply(it) }


            reactivityNode.mark(ReactivityNode.State.DIRTY)
        }

    }


    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NodeId) return false
        if (other.id == id) return true
        return false
    }

}