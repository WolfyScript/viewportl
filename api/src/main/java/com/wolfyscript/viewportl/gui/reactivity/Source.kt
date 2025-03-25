package com.wolfyscript.viewportl.gui.reactivity

interface Source : ReactivityNode {

    fun subscribedBy(subscriber: Subscriber)

    fun unsubscribedBy(subscriber: Subscriber)

    fun clearSubscribers()

}