package com.wolfyscript.viewportl.gui.reactivity

/**
 * Observes [Sources][Source] in a closure and subscribes the associated [Subscriber] to them.
 *
 * The Observer only persists temporarily until the scope is done.
 * When the scope runs again a new Observer is created.
 */
interface Observer {

    /**
     * The [Subscriber] of this Observer
     */
    val subscriber: Subscriber

}