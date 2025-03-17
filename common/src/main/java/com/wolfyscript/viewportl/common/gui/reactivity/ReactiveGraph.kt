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
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.function.ReceiverFunction
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.reactivity.properties.EffectProperty
import com.wolfyscript.viewportl.common.gui.reactivity.properties.ScopeProperty
import com.wolfyscript.viewportl.common.gui.reactivity.properties.SignalProperty
import com.wolfyscript.viewportl.common.gui.reactivity.properties.TriggerProperty
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.reactivity.*
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.apache.commons.lang3.function.TriFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

class ReactiveGraph(private val viewRuntime: ViewRuntimeImpl<*,*>) : ReactiveSource {

    private var owner: NodeId?
    private var observer: NodeId? = null

    // Graph
    private val nodes: MutableMap<NodeId, ReactivityNode<*>> = Object2ObjectOpenHashMap()
    private val nodeSubscribers: SetMultimap<NodeId, NodeId> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val nodeSources: SetMultimap<NodeId, NodeId> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }

    // Owners and Properties
    private val nodeProperties: ListMultimap<NodeId, ScopeProperty> = Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }
    private val nodeOwners: SetMultimap<NodeId, NodeId> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val cleanups: Multimap<NodeId, Cleanup> = Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }

    // Effects that need to be updated
    private val pendingEffects: MutableSet<NodeId> = mutableSetOf()

    init {
        owner = createNode(ReactivityNode.Type.Trigger(), null)
    }

    private fun addNewScopeProperty(property: ScopeProperty) {
        if (owner != null) {
            nodeProperties[owner].add(property)

            property.toNodeId()?.let {
                nodeOwners[it].add(owner)
            }
        }
    }

    fun runWithOwner(owner: NodeId, fn: Runnable) {
        val prevOwner = this.owner
        val prevObserver = observer

        this.owner = owner
        observer = owner
        fn.run()

        this.owner = prevOwner
        observer = prevObserver
    }

    fun runWithObserver(nodeId: NodeId, fn: Runnable) {
        val previousObserver = observer
        val previousOwner = owner

        observer = nodeId
        owner = nodeId
        fn.run()

        observer = previousObserver
        owner = previousOwner
    }

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

    internal fun owner(): Trigger? {
        if (owner != null) {
            nodes[owner]?.let {
                if (it.type is ReactivityNode.Type.Trigger) {
                    return TriggerImpl(it.id)
                }
            }
        }
        return null
    }

    private fun markClean(nodeId: NodeId) {
        val node = nodes[nodeId] ?: return
        node.mark(ReactivityNode.State.CLEAN)
    }

    /**
     * A depth-first DAG (Direct Acyclic Graph) with its origin at the specified node.
     *
     * It recursively marks all child nodes as [ReactivityNode.State.CHECK].
     * If a node is already [ReactivityNode.State.DIRTY] then we mark it as visited ([ReactivityNode.State.DIRTY_MARKED]).
     *
     * This is very similar to the way the framework Leptos does it.
     * Instead of pushing each and every node onto the stack, we push the iterators of child nodes.
     */
    fun markDirty(node: NodeId) {
        val currentNode = nodes[node] ?: return
        mark(currentNode, ReactivityNode.State.DIRTY)

        val children = nodeSubscribers[node]
        val stack: ArrayDeque<Iterator<NodeId>> = ArrayDeque(children.size)
        stack.push(children.iterator())

        while (stack.isNotEmpty()) {
            val (iterResult, iter) = iterateChildren(stack.first())
            when (iterResult) {
                IterResult.CONTINUE -> continue
                IterResult.NEW -> iter?.let { stack.push(it) }
                IterResult.EMPTY -> stack.pop()
            }
        }
    }

    private fun iterateChildren(childIterator: Iterator<NodeId>): Pair<IterResult, Iterator<NodeId>?> {
        if (!childIterator.hasNext()) {
            return Pair(IterResult.EMPTY, null) // When the iterator is done we remove it from the stack
        }
        var child = childIterator.next()

        while (nodes[child] != null) {
            val childNode = nodes[child]!!

            if (childNode.state() == ReactivityNode.State.CHECK || childNode.state() == ReactivityNode.State.DIRTY_MARKED) {
                return Pair(IterResult.CONTINUE, null)
            }

            mark(childNode, ReactivityNode.State.CHECK)

            val childsChildren = nodeSubscribers[child]
            if (childsChildren.isNotEmpty()) {
                if (childsChildren.size == 1) {
                    // No need to iterate over a single element
                    child = childsChildren.elementAt(0)
                    continue
                }
                return Pair(IterResult.NEW, childsChildren.iterator())
            }
            break
        }
        return Pair(IterResult.CONTINUE, null)
    }

    enum class IterResult {
        CONTINUE,
        NEW,
        EMPTY
    }

    private fun mark(node: ReactivityNode<*>, state: ReactivityNode.State) {
        if (state > node.state()) {
            node.mark(state)
        }

        if (node.type is ReactivityNode.Type.Effect && node.id != observer) {
            pendingEffects.add(node.id)
        }

        if (node.state() == ReactivityNode.State.DIRTY) {
            node.mark(ReactivityNode.State.DIRTY_MARKED)
        }
    }

    /**
     * Updates the Node when it is necessary.
     *
     * An update is necessary when any source of the specified node is [ReactivityNode.State.DIRTY].
     *
     * The [ReactivityNode.State.DIRTY] state propagates through the Graph from top to bottom.
     * Therefor this method goes up the Graph, starting at the specified Node, checking recursively if any sources are [ReactivityNode.State.DIRTY].
     *
     * Note that a [ReactivityNode.State.DIRTY] state can stop propagating down. For Example when a Memo doesn't change its value.
     */
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
        val changed = node.update(viewRuntime)
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

    fun cleanupSourcesFor(id: NodeId) {
        for (sourceNode in nodeSources[id]) {
            nodeSubscribers[sourceNode].remove(id)
        }
    }

    private fun currentNodeState(nodeId: NodeId): ReactivityNode.State {
        val reactivityNode = nodes[nodeId] ?: return ReactivityNode.State.CLEAN
        return reactivityNode.state()
    }

    fun untypedNode(id: NodeId): ReactivityNode<*>? {
        return nodes[id]
    }

    inline fun <reified V : ReactivityNode<*>> node(id: NodeId): V? {
        val untyped = untypedNode(id) ?: return null
        if (untyped !is V) {
            throw IllegalStateException("Node ($id) cannot be converted to ${V::class}!")
        }
        return untyped
    }

    inline fun <reified V : Any?> getValue(nodeId: NodeId): V {
        updateIfNecessary(nodeId)
        val node = node<ReactivityNode<V>>(nodeId) ?: throw IllegalArgumentException("Cannot find reactive node $nodeId of type ${V::class}!")
        return node.value
    }

    fun <V : Any?> getValue(nodeId: NodeId, type: Class<V>): V {
        updateIfNecessary(nodeId)
        val node = node<ReactivityNode<V>>(nodeId) ?: throw IllegalArgumentException("Cannot find reactive node $nodeId of type $type!")
        return node.value
    }

    inline fun <reified V : Any?> setValue(nodeId: NodeId, value: V) {
        node<ReactivityNode<V>>(nodeId)?.value = value
        markDirty(nodeId)
    }

    fun <V : Any?> setValue(nodeId: NodeId, type: Class<V>, value: V) {
        node<ReactivityNode<V>>(nodeId)?.value = value
        markDirty(nodeId)
    }

    private fun <V : Any?> createNode(
        type: ReactivityNode.Type<V>,
        initialValue: V,
        state: ReactivityNode.State = ReactivityNode.State.CLEAN
    ): NodeId {
        val id = NodeId((nodes.size + 1).toLong(), viewRuntime)
        nodes[id] = ReactivityNode(id, initialValue, type, state)
        return id
    }

    override fun createTrigger(): Trigger {
        val id = createNode(ReactivityNode.Type.Trigger(), null)
        addNewScopeProperty(TriggerProperty(id))
        return TriggerImpl(id)
    }

    override fun <T : Any?> createSignal(
        valueType: Class<T>,
        defaultValueProvider: ReceiverFunction<ViewRuntime<*,*>, T>
    ): ReadWriteSignal<T> {
        val id = createNode(ReactivityNode.Type.Signal(), with(defaultValueProvider) { viewRuntime.apply() })
        addNewScopeProperty(SignalProperty(id))
        return ReadWriteSignalImpl(id, valueType)
    }

    override fun <T : Any?> createMemoEffect(initialValue: T, effect: ReceiverFunction<T, T>): Effect {
        return createCustomEffect<T>(initialValue, EffectState(effect))
    }

    fun <T : Any?> createCustomEffect(value: T, effect: AnyComputation<T>): Effect {
        val id = createNode(ReactivityNode.Type.Effect(effect), value, ReactivityNode.State.DIRTY)
        addNewScopeProperty(EffectProperty(id))

        viewRuntime.viewportl.scafall.scheduler.syncTask(viewRuntime.viewportl.scafall.corePlugin) { // TODO: Is there a better way to schedule them?
            updateIfNecessary(id)
        }

        return EffectImpl(id)
    }

    override fun <T : Any?> createMemo(initialValue: T, valueType: Class<T>, fn: Function<T?, T>): Memo<T> {
        val reactivityNodeId = createNode(ReactivityNode.Type.Memo<T>(MemoState {
            val newValue = fn.apply(it)
            Pair(newValue, newValue != it)
        }), initialValue, ReactivityNode.State.DIRTY)
        addNewScopeProperty(EffectProperty(reactivityNodeId))
        return MemoImpl(reactivityNodeId, valueType)
    }

    override fun createCleanup(cleanup: Cleanup) {
        if (owner != null) {
            cleanups.put(owner, cleanup)
        }
    }

    override fun <T: Any> resourceSync(fetch: BiFunction<Viewportl, ViewRuntime<*, *>, Result<T>>): Resource<T> {
        val nodeId = createNode(ReactivityNode.Type.Resource<T> { runtime, node ->
            viewRuntime.viewportl.scafall.scheduler.syncTask(viewRuntime.viewportl.scafall.corePlugin) {
                val fetchedValue = fetch.apply(viewRuntime.viewportl, viewRuntime)
                node.value = Optional.of(fetchedValue)

                for (id in runtime.reactiveSource.nodeSubscribers[node.id]) {
                    runtime.reactiveSource.markDirty(id)
                }
                runtime.reactiveSource.runEffects()
            }

        }, Optional.empty<Result<T>>(), ReactivityNode.State.DIRTY)

        addNewScopeProperty(EffectProperty(nodeId))
        return ResourceImpl(nodeId, Optional::class as Class<Optional<Result<T>>>)
    }

    override fun <T : Any> resourceSync(
        vararg input: ReadWriteSignal<*>,
        fetch: (Viewportl, ViewRuntime<*, *>) -> Result<T>
    ): Resource<T> {
        TODO("Not yet implemented")

    }

    override fun <T: Any> resourceAsync(fetch: BiFunction<Viewportl, ViewRuntime<*,*>, Result<T>>): Resource<T> {
        val nodeId = createNode(ReactivityNode.Type.Resource<T> { runtime, node ->
            viewRuntime.viewportl.scafall.scheduler.asyncTask(viewRuntime.viewportl.scafall.corePlugin) {
                val fetchedValue = fetch.apply(viewRuntime.viewportl, viewRuntime)
                node.value = Optional.of(fetchedValue)

                for (id in runtime.reactiveSource.nodeSubscribers[node.id]) {
                    runtime.reactiveSource.markDirty(id)
                }
                runtime.reactiveSource.runEffects()
            }
        }, Optional.empty<Result<T>>(), ReactivityNode.State.DIRTY)

        addNewScopeProperty(EffectProperty(nodeId))
        return ResourceImpl(nodeId, Optional.empty<Result<T>>().javaClass)
    }

    fun subscribe(node: NodeId) {
        if (observer != null) {
            nodeSubscribers[node].add(observer)
            nodeSources[observer].add(node)
        } else {
//            throw IllegalStateException("Cannot subscribe to observer: Observer is null")
        }
    }

    override fun toString(): String {
        return renderState()
    }

}
