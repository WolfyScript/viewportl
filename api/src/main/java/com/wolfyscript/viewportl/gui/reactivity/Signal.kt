package com.wolfyscript.viewportl.gui.reactivity

import kotlin.reflect.KProperty

interface Signal<T> : ReactivityNode, Source {

    fun set(value: T)

    fun update(fn: (T) -> Unit)

    fun get(): T

    operator fun getValue(thisRef: Any?, property: KProperty<*>) : T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}