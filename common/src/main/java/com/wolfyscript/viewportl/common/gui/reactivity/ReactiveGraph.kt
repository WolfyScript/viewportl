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

import com.google.common.collect.ListMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import com.wolfyscript.scafall.function.ReceiverFunction
import com.wolfyscript.scafall.scheduling.Task
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.reactivity.Observer
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.reactivity.effect.EffectImpl
import com.wolfyscript.viewportl.common.gui.reactivity.memo.MemoImpl
import com.wolfyscript.viewportl.common.gui.reactivity.properties.ScopeProperty
import com.wolfyscript.viewportl.common.gui.reactivity.signal.SignalImpl
import com.wolfyscript.viewportl.common.gui.reactivity.signal.TriggerImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.reactivity.*
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*

class ReactiveGraph(private val viewRuntime: ViewRuntimeImpl<*, *>) : ReactiveSource {

    private var nextNodeId: Long = 0

    // Graph
    private val nodes: MutableMap<NodeId, ReactivityNode> = Object2ObjectOpenHashMap()
    private val rootOwner: OwnerImpl = OwnerImpl(viewRuntime, null)
    private val associatedOwners: Map<NodeId, OwnerImpl> = Object2ObjectOpenHashMap()

    // State
    internal var owner: OwnerImpl? = rootOwner
    internal var observer: Observer? = null

    // OLD
    private val nodeSubscribers: SetMultimap<NodeId, NodeId> =
        Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val nodeSources: SetMultimap<NodeId, NodeId> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }

    // Owners and Properties
    private val nodeProperties: ListMultimap<NodeId, ScopeProperty> =
        Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }
    private val nodeOwners: SetMultimap<NodeId, NodeId> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val cleanups: Multimap<NodeId, Cleanup> = Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }

    // Effects that need to be updated
    private val pendingEffects: MutableSet<NodeId> = mutableSetOf()

    fun runEffects() {
        pendingEffects.removeAll {
            updateIfNecessary(it)
            true
        }
    }

    fun renderState(): String {
        return buildString {
            appendLine("-------- [Reactive Graph] --------")
            appendLine("Pending: $pendingEffects")
            appendLine("Nodes (${nodes.size}): ")
            for (node in nodes) {
                appendLine("  ${node.key}: ${node.value.type.javaClass.simpleName} = ${node.value.value}")
                val subs = nodeSubscribers[node.key]
                if (subs.isNotEmpty()) {
                    appendLine("    Subscribers: $subs")
                }
                val sources = nodeSources[node.key]
                if (sources.isNotEmpty()) {
                    appendLine("    Sources: $sources")
                }
                val owners = nodeOwners[node.key]
                if (owners.isNotEmpty()) {
                    appendLine("    Owners: $owners")
                }
                val properties = nodeProperties[node.key]
                if (properties.isNotEmpty()) {
                    appendLine("    Properties: ${properties.map { it.toNodeId() }}")
                }
            }
        }
    }

    private fun markClean(nodeId: NodeId) {}

    fun updateIfNecessary(nodeId: NodeId) {
        if (currentNodeState(nodeId) == ReactivityNode.State.CHECK) {
            // When a node is marked CHECK then check its sources for changes
            for (source in nodeSources[nodeId]) {
                updateIfNecessary(source)
                if (currentNodeState(nodeId) >= ReactivityNode.State.DIRTY) {
                    // Once one of the sources marks this node dirty it is not necessary to check the other sources
                    break
                }
            }
        }

        if (currentNodeState(nodeId) >= ReactivityNode.State.DIRTY) {
            cleanupNode(nodeId) // When dirty clean all properties and values
            update(nodeId) // Then update the node
        }
        markClean(nodeId)
    }

    /**
     * Updates the specified node and marks its subscribers [ReactivityNode.State.DIRTY] when the nodes value changed.
     */
    private fun update(nodeId: NodeId) {
        val node = nodes[nodeId] ?: return
        val changed = node.updateIfNecessary(viewRuntime)
        if (changed) { // (signals always true, memos only when their value changed)
            // Mark the subscribers (children) dirty
            for (subscriber in nodeSubscribers[nodeId]) {
                nodes[subscriber]?.mark(ReactivityNode.State.DIRTY)
            }
        }
        markClean(nodeId)
    }

    /**
     * Cleans all properties and runs all cleanups of the specified Node.
     */
    private fun cleanupNode(id: NodeId) {
        val prevObserver = observer
        for (cleanup in cleanups.removeAll(id)) {
            cleanup.run()
        }
        observer = prevObserver

        for (property in nodeProperties.removeAll(id)) {
            cleanupProperty(property)
        }
    }

    /**
     * Cleans the specified property and child properties recursively.
     */
    private fun cleanupProperty(property: ScopeProperty) {
        property.toNodeId()?.let { nodeId ->
            for (cleanup in cleanups.removeAll(nodeId)) {
                cleanup.run()
            }
            // Clean child properties
            nodeProperties.removeAll(nodeId).forEach { cleanupProperty(it) }
            // Subscribers should no longer listen to this now removed node
            nodeSubscribers.removeAll(nodeId).forEach { subscriber ->
                nodeSources[subscriber].remove(nodeId)
            }
            // Remove all tracked sources
            nodeSources.removeAll(nodeId)
            // Remove the node
            nodes.remove(nodeId)
        }
    }

    private fun currentNodeState(nodeId: NodeId): ReactivityNode.State {
        val reactivityNode = nodes[nodeId] ?: return ReactivityNode.State.CLEAN
        return reactivityNode.state()
    }

    override fun createTrigger(): Trigger {
        val id = NodeId(nextNodeId++, viewRuntime)
        return TriggerImpl(id)
    }

    override fun <T : Any?> createSignal(
        valueType: Class<T>,
        defaultValueProvider: ReceiverFunction<ViewRuntime<*, *>, T>,
    ): Signal<T> {
        val id = NodeId(nextNodeId++, viewRuntime)
        return SignalImpl<T>(id, with(defaultValueProvider) { viewRuntime.apply() })
    }

    override fun createEffect(effect: () -> Unit): Effect {
        return EffectImpl(NodeId(nextNodeId++, viewRuntime), effect)
    }

    override fun <T : Any?> createMemoEffect(initialValue: T, effect: (T) -> T): Effect {
        TODO()
    }

    override fun <T : Any?> createMemo(initialValue: T, valueType: Class<T>, fn: (T) -> T): Memo<T> {
        val id = NodeId(nextNodeId++, viewRuntime)
        return MemoImpl(id, initialValue, fn)
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
    ): Pair<ReadWriteSignal<Result<T>?>, () -> Unit> {
        val value = createSignal<Result<T>?> { initialValue }
        val inputMemo = createMemo<I?>(null, inputType) { input() }

        var initiated: Boolean = initialValue != null
        var task: Task? = null

        fun completeFetch(fetchedValue: Result<T>) {
            task = null;
            initiated = true
            value.set(fetchedValue)
            viewRuntime.viewportl.scafall.scheduler.syncTask(viewRuntime.viewportl.scafall.corePlugin) {
                viewRuntime.reactiveSource.runEffects()
            }
        }

        val schedule = fun() {
            if (task != null) {
                return
            }
            val inputCopy = inputMemo.get()
            if (inputCopy != null) {
                value.set(null)
                viewRuntime.viewportl.scafall.scheduler.syncTask(viewRuntime.viewportl.scafall.corePlugin) {
                    viewRuntime.reactiveSource.runEffects()
                }

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
