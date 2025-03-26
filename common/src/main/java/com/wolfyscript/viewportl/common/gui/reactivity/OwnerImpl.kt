package com.wolfyscript.viewportl.common.gui.reactivity

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.reactivity.Cleanup
import com.wolfyscript.viewportl.gui.reactivity.Owner

class OwnerImpl(
    val runtime: ViewRuntimeImpl<*,*>,
    override val parent: Owner? = runtime.reactiveSource.owner // Use the current owner as the parent
) : Owner {
    override val nodes: MutableList<NodeId> = mutableListOf()
    override val children: MutableList<Owner> = mutableListOf()
    override val cleanups: MutableList<Cleanup> = mutableListOf()

    fun addCleanup(cleanup: Cleanup) {
        cleanups.add(cleanup)
    }

    override fun runCleanups() {
        for (cleanup in cleanups) {
            cleanup.run()
        }
    }

    fun clear() {
        nodes.clear()
        children.clear()
        cleanups.clear()
    }

    override fun <T> acquire(fn: () -> T) : T {
        val prevOwner = runtime.reactiveSource.owner
        runtime.reactiveSource.owner = this
        val returnVal = fn()
        runtime.reactiveSource.owner = prevOwner
        return returnVal
    }
}