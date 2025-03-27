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

package com.wolfyscript.viewportl.common.gui.reactivity.signal

import com.wolfyscript.viewportl.common.gui.reactivity.NodeId
import com.wolfyscript.viewportl.common.gui.reactivity.ReactivityNodeImpl
import com.wolfyscript.viewportl.gui.reactivity.Subscriber
import com.wolfyscript.viewportl.gui.reactivity.Trigger
import it.unimi.dsi.fastutil.objects.ObjectArraySet

class TriggerImpl(
    id: NodeId,
) : Trigger, ReactivityNodeImpl(id) {

    val subscribers: MutableSet<Subscriber> = ObjectArraySet()

    override fun track() {
        id.runtime.reactiveSource.observer?.subscriber?.subscribeTo(this)
    }

    override fun trigger() {
        markDirty()
    }

    override fun markDirty() {
        notifySubscribers()
    }

    override fun markCheck() {}

    override fun notifySubscribers() {
        for (subscriber in subscribers) {
            subscriber.markDirty()
        }
    }

    override fun updateIfNecessary(): Boolean {
        return false
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
}