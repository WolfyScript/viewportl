package com.wolfyscript.viewportl.common.gui.reactivity.signal

import com.wolfyscript.viewportl.common.gui.reactivity.NodeId
import com.wolfyscript.viewportl.common.gui.reactivity.ReactivityNodeImpl
import com.wolfyscript.viewportl.gui.reactivity.Signal
import com.wolfyscript.viewportl.gui.reactivity.Source
import com.wolfyscript.viewportl.gui.reactivity.Subscriber
import kotlin.reflect.KProperty

class SignalImpl<T>(
    id: NodeId,
    initialValue: T,
) : Signal<T>, ReactivityNodeImpl(id), Source {

    val subscribers: MutableList<Subscriber> = mutableListOf()
    var value = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(value)
    }

    override fun set(value: T) {
        this.value = value
        markDirty()
    }

    override fun update(fn: (T) -> Unit) {
        fn(value)
        markDirty()
    }

    override fun get(): T {
        id.runtime.reactiveSource.observer?.subscriber?.subscribeTo(this)
        updateIfNecessary()
        return this.value
    }

    override fun notifySubscribers() {
        // Subscribers of this signal are marked DIRTY, because this signal was updated
        for (subscriber in subscribers) {
            subscriber.markDirty()
        }
    }

    override fun markDirty() {
        // No need to mark the Signal DIRTY, just propagate the state down to Memos and Effects
        notifySubscribers()
    }

    override fun markCheck() {
        // A Signal is either DIRTY or CLEAN, no need for CHECK as it has no sources!
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