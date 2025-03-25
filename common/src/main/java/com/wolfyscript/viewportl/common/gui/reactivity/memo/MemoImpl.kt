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

package com.wolfyscript.viewportl.common.gui.reactivity.memo

import com.wolfyscript.viewportl.common.gui.reactivity.NodeId
import com.wolfyscript.viewportl.common.gui.reactivity.ObserverImpl
import com.wolfyscript.viewportl.common.gui.reactivity.OwnerImpl
import com.wolfyscript.viewportl.common.gui.reactivity.ReactivityNodeImpl
import com.wolfyscript.viewportl.gui.reactivity.Memo
import com.wolfyscript.viewportl.gui.reactivity.ReactivityNode
import com.wolfyscript.viewportl.gui.reactivity.Source
import com.wolfyscript.viewportl.gui.reactivity.Subscriber
import kotlin.reflect.KProperty

class MemoImpl<V : Any?>(
    id: NodeId,
    initialValue: V,
    val fn: (V) -> V,
    val owner: OwnerImpl = OwnerImpl(id.runtime),
) : Memo<V>, ReactivityNodeImpl(id) {

    val sources: MutableList<Source> = mutableListOf()
    val subscribers: MutableList<Subscriber> = mutableListOf()

    var value: V = initialValue

    override fun get(): V {
        id.runtime.reactiveSource.observer?.subscriber?.subscribeTo(this)
        return value
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return get()
    }

    override fun updateIfNecessary(): Boolean {
        val shouldUpdate = when(state) {
            ReactivityNode.State.CLEAN -> false
            ReactivityNode.State.CHECK -> {
                sources.any {
                    it.updateIfNecessary() || state == ReactivityNode.State.DIRTY // The source may mark this as Dirty again
                }
            }
            ReactivityNode.State.DIRTY -> true
        }

        if (shouldUpdate) {
            val newValue = owner.acquire {
                this.observe {
                    return@observe fn(value)
                }
            }
            value = newValue
        }

        return false
    }

    override fun markCheck() {
        if (state != ReactivityNode.State.DIRTY) {
            state = ReactivityNode.State.CHECK
        }
        for (subscriber in subscribers) {
            subscriber.markCheck()
        }
    }

    override fun markDirty() {
        super.markDirty()
        notifySubscribers()
    }

    override fun notifySubscribers() {
        for (subscriber in subscribers) {
            subscriber.markCheck()
        }
    }

    override fun subscribedBy(subscriber: Subscriber) {
        subscribers.add(subscriber)
    }

    override fun unsubscribedBy(subscriber: Subscriber) {
        subscribers.remove(subscriber)
    }

    override fun clearSubscribers() {
        subscribers.clear()
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
