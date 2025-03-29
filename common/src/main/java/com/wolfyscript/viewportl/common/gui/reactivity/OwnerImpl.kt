package com.wolfyscript.viewportl.common.gui.reactivity

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.reactivity.effect.EffectImpl
import com.wolfyscript.viewportl.gui.reactivity.Cleanup
import com.wolfyscript.viewportl.gui.reactivity.Owner
import com.wolfyscript.viewportl.gui.reactivity.ReactivityNode
import com.wolfyscript.viewportl.gui.reactivity.Source
import com.wolfyscript.viewportl.gui.reactivity.Subscriber
import it.unimi.dsi.fastutil.objects.ObjectArraySet

class OwnerImpl(
    val runtime: ViewRuntimeImpl<*,*>,
    override val parent: OwnerImpl? = runtime.reactiveSource.owner // Use the current owner as the parent
) : Owner {
    override val nodes: MutableSet<ReactivityNode> = ObjectArraySet()
    override val children: MutableSet<OwnerImpl> = ObjectArraySet()
    override val cleanups: MutableList<Cleanup> = mutableListOf()

    init {
        parent?.children?.add(this)
    }

    override fun dispose() {
        for (child in children) {
            child.dispose()
        }
        children.clear()
        runCleanups()
        clear()
    }

    fun addCleanup(cleanup: Cleanup) {
        cleanups.add(cleanup)
    }

    fun runCleanups() {
        for (cleanup in cleanups) {
            cleanup.run()
        }
    }

    fun clear() {
        for (node in nodes) {
            if (node is Subscriber) {
                node.clearSources()
            }
            if (node is Source) {
                node.clearSubscribers()
            }
            if (node is EffectImpl) {
                node.state = ReactivityNode.State.CLEAN
            }
        }
        nodes.clear()
        cleanups.clear()
    }

    override fun <T> acquire(fn: () -> T) : T {
        val prevOwner = runtime.reactiveSource.owner
        runtime.reactiveSource.owner = this
        val returnVal = fn()
        runtime.reactiveSource.owner = prevOwner
        return returnVal
    }

    override fun toString(): String {
        return buildString {
            append("{ ")
            append(super.toString().replace("com.wolfyscript.viewportl.common.gui.reactivity.", ""))
            append(": Nodes: ${nodes.size}, children: (${children.size})[$children] }")
        }
    }
}