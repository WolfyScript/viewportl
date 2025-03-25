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

    override fun get(): T {
        id.runtime.reactiveSource.observer?.subscriber?.subscribeTo(this)
        updateIfNecessary()
        return this.value
    }

    override fun notifySubscribers() {
        for (subscriber in subscribers) {
            subscriber.markDirty()
        }
    }

    override fun markDirty() {
        notifySubscribers()
    }

    override fun markCheck() {}

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