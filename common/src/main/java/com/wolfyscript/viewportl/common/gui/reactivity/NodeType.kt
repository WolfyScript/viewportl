package com.wolfyscript.viewportl.common.gui.reactivity

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl

interface NodeType<T : Any?> {

    fun runUpdate(runtime: ViewRuntimeImpl<*,*>, reactivityNode: ReactivityNode<T>) : Boolean = true

    class Trigger : NodeType<Any?>

    class Signal<T : Any?> : NodeType<T>

    class Effect<T : Any?>(private val fn: AnyComputation<T>) : NodeType<T> {

        override fun runUpdate(runtime: ViewRuntimeImpl<*,*>, reactivityNode: ReactivityNode<T>): Boolean {
            runtime.reactiveSource.runWithObserver(reactivityNode.id) {
                runtime.reactiveSource.cleanupSourcesFor(reactivityNode.id)
                computation().run(runtime, reactivityNode.value) { reactivityNode.value = it }
            }
            return true
        }

        fun computation(): AnyComputation<T> = fn

    }

    class Memo<T : Any?>(private val fn: AnyComputation<T>) : NodeType<T> {

        override fun runUpdate(runtime: ViewRuntimeImpl<*,*>, reactivityNode: ReactivityNode<T>): Boolean {
            var changed = false
            runtime.reactiveSource.runWithObserver(reactivityNode.id) {
                runtime.reactiveSource.cleanupSourcesFor(reactivityNode.id)
                changed = computation().run(runtime, reactivityNode.value) { reactivityNode.value = it }
            }
            return changed
        }

        fun computation(): AnyComputation<T> = fn

    }

}