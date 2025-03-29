package com.wolfyscript.viewportl.gui.reactivity

/**
 * The Owner is usually bound to an [Effect], or [Memo].
 *
 * It holds all the [ReactivityNodes][ReactivityNode] and [Cleanups][Cleanup] within its scope.
 * Once the scope is exited or updated, the Owner is cleared, cleanups run and nodes disposed.
 */
interface Owner {

    val parent: Owner?
    val nodes: Set<ReactivityNode>
    val children: Set<Owner>
    val cleanups: List<Cleanup>

    /**
     * Runs the function within this owner.
     *
     * This owner acquires all the [ReactivityNode]s, [Cleanup]s, and [Owner]s created within the function.
     */
    fun <T> acquire(fn: () -> T): T

    fun dispose()

}