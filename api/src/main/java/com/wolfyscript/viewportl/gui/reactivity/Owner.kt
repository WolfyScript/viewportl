package com.wolfyscript.viewportl.gui.reactivity

interface Owner {

    val parent: Owner?
    val nodes: List<NodeId>
    val children: List<Owner>
    val cleanups: List<Cleanup>

    /**
     * Runs the function within this owner.
     *
     * This owner acquires all the [ReactivityNode]s, [Cleanup]s, and [Owner]s created within the function.
     */
    fun <T> acquire(fn: () -> T): T

    fun runCleanups()

}