package com.wolfyscript.viewportl.gui.reactivity

interface Subscriber : ReactivityNode {

    fun subscribeTo(source: Source)

    fun clearSources()

    /**
     * Runs the specified function with this Subscriber as an [Observer].
     */
    fun <T> observe(fn: () -> T): T

}