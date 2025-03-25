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

package com.wolfyscript.viewportl.common.gui.reactivity.effect

import com.wolfyscript.viewportl.common.gui.reactivity.NodeId
import com.wolfyscript.viewportl.common.gui.reactivity.ObserverImpl
import com.wolfyscript.viewportl.common.gui.reactivity.OwnerImpl
import com.wolfyscript.viewportl.common.gui.reactivity.ReactivityNodeImpl
import com.wolfyscript.viewportl.gui.reactivity.Effect
import com.wolfyscript.viewportl.gui.reactivity.Owner
import com.wolfyscript.viewportl.gui.reactivity.ReactivityNode
import com.wolfyscript.viewportl.gui.reactivity.Source

class EffectImpl(
    id: NodeId,
    val fn: () -> Unit,
    val owner: Owner = OwnerImpl(id.runtime),
) : Effect, ReactivityNodeImpl(id) {

    val sources: MutableList<Source> = mutableListOf<Source>()

    override fun notifySubscribers() {}

    override fun updateIfNecessary(): Boolean {
        val needsToUpdate = when (state) {
            ReactivityNode.State.CLEAN -> false
            ReactivityNode.State.CHECK -> sources.any { it.updateIfNecessary() }
            ReactivityNode.State.DIRTY -> true
        }

        // TODO: Queue effect execution instead of executing it here

        if (needsToUpdate) {

        }

        return needsToUpdate
    }

    override fun markCheck() {
        super.markCheck()
        updateIfNecessary()
    }

    override fun markDirty() {
        super.markDirty()
        updateIfNecessary()
    }

    override fun subscribeTo(source: Source) {
        sources.add(source)
    }

    override fun clearSources() {
        sources.clear()
    }

    override fun <T> observe(fn: () -> T): T {
        val prevObserver = id.runtime.reactiveSource.observer
        id.runtime.reactiveSource.observer = ObserverImpl(this)
        val returnValue = fn()
        id.runtime.reactiveSource.observer = prevObserver
        return returnValue
    }

}