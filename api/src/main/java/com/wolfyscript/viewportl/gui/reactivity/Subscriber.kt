package com.wolfyscript.viewportl.gui.reactivity

/**
 * Subscribes to [Sources][Source] and updates when their values change
 */
interface Subscriber : ReactivityNode {

    /**
     * Subscribes to the specified [Source]
     *
     * @param source The source to subscribe to
     */
    fun subscribeTo(source: Source)

    /**
     * Removes all the subscribed to [Sources][Source]
     */
    fun clearSources()

    /**
     * Runs the specified function with this Subscriber as an [Observer].
     *
     * This Subscriber subscribes to all [Sources][Source] used within this function
     */
    fun <T> observe(fn: () -> T): T

}