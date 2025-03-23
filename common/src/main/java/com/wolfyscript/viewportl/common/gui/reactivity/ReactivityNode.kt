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

package com.wolfyscript.viewportl.common.gui.reactivity

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl

class ReactivityNode<V>(
    val id: NodeId,
    var value: V,
    val type: NodeType<V>,
    private var state: State = State.CLEAN
) {

    fun mark(state: State) {
        this.state = state
    }

    fun state() : State {
        return state
    }

    fun update(viewRuntime: ViewRuntimeImpl<*,*>) : Boolean {
        return type.runUpdate(viewRuntime, this)
    }

    fun subscribe() {
        id.runtime.reactiveSource.subscribe(id)
    }

    override fun toString(): String {
        return "{${id} (${type}): ${value}}"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ReactivityNode<*>) {
            return id == other.id
        }
        return false
    }

    enum class State {
        /**
         * Node is clean, it does not need to be updated
         */
        CLEAN,

        /**
         * Node may have changed, needs to be checked
         */
        CHECK,

        /**
         * Node has changed, needs to update
         */
        DIRTY,

        DIRTY_MARKED
    }

}