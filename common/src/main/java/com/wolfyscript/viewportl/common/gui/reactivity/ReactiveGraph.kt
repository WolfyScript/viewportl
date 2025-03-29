/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wolfyscript.viewportl.common.gui.reactivity

import com.wolfyscript.scafall.function.ReceiverFunction
import com.wolfyscript.scafall.scheduling.Task
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.reactivity.Observer
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.reactivity.effect.EffectImpl
import com.wolfyscript.viewportl.common.gui.reactivity.memo.MemoImpl
import com.wolfyscript.viewportl.common.gui.reactivity.signal.SignalImpl
import com.wolfyscript.viewportl.common.gui.reactivity.signal.TriggerImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.reactivity.*

class ReactiveGraph(private val viewRuntime: ViewRuntimeImpl<*, *>) : ReactiveSource {

    private var nextId: Long = 0
    private val nextNodeId: NodeId
        get() {
            return NodeId(nextId++, viewRuntime)
        }

    // Graph
    private val rootOwner: OwnerImpl = OwnerImpl(viewRuntime, null)

    // State
    internal var owner: OwnerImpl? = rootOwner
    internal var observer: Observer? = null

    // Effects that need to be updated
    private val pendingEffects: MutableSet<EffectImpl> = mutableSetOf()
    private var scheduler: Task? = null

    fun initScheduler() {
        scheduler = viewRuntime.viewportl.scafall.scheduler.asyncTimerTask(viewRuntime.viewportl.scafall.corePlugin, {
            // TODO: Check if blocked by interaction
            runEffects()
        }, 1, 2)
    }

    fun exit() {
        scheduler?.cancel()
    }

    @Synchronized
    fun scheduleEffect(effect: EffectImpl) {
        pendingEffects.add(effect)
    }

    @Synchronized
    fun runEffects() {
        val pending = pendingEffects.toList()
        pendingEffects.clear()
        for (effect in pending) {
            effect.execute()
        }
    }

    fun renderState(): String {
        return buildString {
            appendLine("-------- [Reactive Graph] --------")
            appendLine("Pending: $pendingEffects")
        }
    }

    override fun createTrigger(): Trigger {
        return TriggerImpl(nextNodeId)
    }

    override fun <T : Any?> createSignal(
        valueType: Class<T>,
        defaultValueProvider: ReceiverFunction<ViewRuntime<*, *>, T>,
    ): Signal<T> {
        val signal = SignalImpl<T>(nextNodeId, with(defaultValueProvider) { viewRuntime.apply() })
        owner?.nodes?.add(signal)
        return signal
    }

    override fun createEffect(effect: () -> Unit): Effect {
        val effect = EffectImpl(nextNodeId, effect)
        owner?.nodes?.add(effect)
        return effect
    }

    override fun <T : Any?> createMemo(initialValue: T, valueType: Class<T>, fn: (T) -> T): Memo<T> {
        val memo = MemoImpl(nextNodeId, initialValue, fn)
        owner?.nodes?.add(memo)
        return memo
    }

    fun <I, T> createLink(input: () -> I, getter: (I, ViewRuntime<*,*>) -> T, setter: (T, ViewRuntime<*,*>) -> Unit) { }

    override fun createCleanup(cleanup: Cleanup) {
        owner?.addCleanup(cleanup)
    }

    override fun <I, T : Any> resourceAsync(
        initialValue: Result<T>?,
        inputType: Class<I?>,
        input: () -> I?,
        fetch: (I, Viewportl, ViewRuntime<*, *>) -> Result<T>,
    ): Pair<Signal<Result<T>?>, () -> Unit> {
        val value = createSignal<Result<T>?> { initialValue }
        val inputMemo = createMemo<I?>(null, inputType) { input() }

        var initiated: Boolean = initialValue != null
        var task: Task? = null

        fun completeFetch(fetchedValue: Result<T>) {
            task = null;
            initiated = true
            value.set(fetchedValue)
        }

        val schedule = fun() {
            if (task != null) {
                return
            }
            val inputCopy = inputMemo.get()
            if (inputCopy != null) {
                value.set(null)

                task = viewRuntime.viewportl.scafall.scheduler.asyncTask(viewRuntime.viewportl.scafall.corePlugin) {
                    val fetchedValue = fetch(inputCopy, viewRuntime.viewportl, viewRuntime)
                    completeFetch(fetchedValue)
                }
            }
        }

        createEffect { schedule() }

        return Pair(value, schedule)
    }

    override fun toString(): String {
        return renderState()
    }

}
