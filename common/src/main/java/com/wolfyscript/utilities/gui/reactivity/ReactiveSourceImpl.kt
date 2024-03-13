/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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
package com.wolfyscript.utilities.gui.reactivity

import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.gui.functions.SignalableReceiverFunction
import com.wolfyscript.utilities.platform.Platform
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.apache.commons.lang3.function.TriFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import kotlin.reflect.KClass

class ReactiveSourceImpl(private val viewRuntime: ViewRuntimeImpl) : ReactiveSource {

    private val nodes: MutableMap<NodeId, ReactivityNode<*>> = Object2ObjectOpenHashMap()
    private val rootNodes: MutableMap<NodeId, ReactivityNode<*>> = Object2ObjectOpenHashMap()
    private val nodeSubscribers: MutableMap<NodeId, MutableSet<NodeId>> = Object2ObjectOpenHashMap()
    private val nodeSources: MutableMap<NodeId, MutableSet<NodeId>> = Object2ObjectOpenHashMap()
    private val pendingEffects: MutableList<NodeId> = ArrayList()

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
     * We do this like Leptos here. Instead of pushing each and every node onto the stack, we push the iterators of child nodes.
     */
    fun markDirty(node: NodeId) {
        val currentNode = nodes[node] ?: return
        currentNode.mark(ReactivityNode.State.DIRTY)

        val children = nodeSubscribers[node]!!
        val stack: ArrayDeque<Iterator<NodeId>> = ArrayDeque(children.size)
        stack.push(children.iterator())

        while (stack.isNotEmpty()) {
            val childIterator = stack.poll()
            val (iterResult, iter) = iterateChildren(childIterator)
            when (iterResult) {
                IterResult.CONTINUE -> continue
                IterResult.NEW -> iter?.let { stack.push(it) }
                IterResult.EMPTY -> stack.pop()
            }
        }
    }

    private fun iterateChildren(childIterator: Iterator<NodeId>): Pair<IterResult, Iterator<NodeId>?> {
        if (!childIterator.hasNext()) {
            return Pair(IterResult.EMPTY, null) // When the iterator is done we remove from the stack
        }
        var child = childIterator.next()

        while (nodes[child] != null) {
            val childNode = nodes[child]!!

            if (childNode.state() == ReactivityNode.State.CHECK || childNode.state() == ReactivityNode.State.DIRTY_MARKED) {
                return Pair(IterResult.CONTINUE, null)
            }

            // TODO: Maybe move to an extra function
            if (ReactivityNode.State.CHECK > childNode.state()) {
                childNode.mark(ReactivityNode.State.CHECK)
            }
            if (childNode.type is ReactivityNode.Type.Effect) {
                pendingEffects.add(child)
            }
            if (childNode.state() == ReactivityNode.State.DIRTY) {
                childNode.mark(ReactivityNode.State.DIRTY_MARKED)
            }
            //////////

            val childsChildren = nodeSubscribers[child]!!

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

    fun runEffects() {
        for (pendingEffect in pendingEffects) {
            updateIfNecessary(pendingEffect)
        }
    }

    private fun updateIfNecessary(nodeId: NodeId) {
        if (currentNodeState(nodeId) == ReactivityNode.State.CHECK) {
            for (source in nodeSources[nodeId]!!) {
                updateIfNecessary(source)

                if (currentNodeState(nodeId) >= ReactivityNode.State.DIRTY) {
                    break
                }
            }
        }

        if (currentNodeState(nodeId) >= ReactivityNode.State.DIRTY) {
            update(nodeId)
        }
        markClean(nodeId)
    }

    private fun update(nodeId: NodeId) {
        val node = nodes[nodeId] ?: return

        // Mark the subscribers (children) dirty
        if (node.update(viewRuntime)) {
            for (subscriber in nodeSubscribers[nodeId]!!) {
                nodes[subscriber]?.mark(ReactivityNode.State.DIRTY)
            }
        }
        markClean(nodeId)
    }

    fun currentNodeState(nodeId: NodeId): ReactivityNode.State {
        val reactivityNode = nodes[nodeId] ?: return ReactivityNode.State.CLEAN
        return reactivityNode.state()
    }

    fun disposeNode(nodeId: NodeId) {
        nodeSources.remove(nodeId)
        nodeSubscribers.remove(nodeId)
        nodes.remove(nodeId)
    }

    private fun mark(nodeId: NodeId, reactivityNode: ReactivityNode<*>, level: ReactivityNode.State) {


    }


    enum class IterResult {
        CONTINUE,
        NEW,
        EMPTY
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

    inline fun <reified V: Any> getValue(nodeId: NodeId) : V? {
        val reactivityNode = node<ReactivityNode<V>>(nodeId)
        return reactivityNode?.value
    }

    inline fun <reified V: Any> setValue(nodeId: NodeId, value: V?) {
        val reactivityNode = node<ReactivityNode<V>>(nodeId)
        reactivityNode?.value = value
    }

    fun <T: Any> setValue(nodeId: NodeId, valueType: KClass<T>, value: T?) {
        val reactivityNode = node<ReactivityNode<T>>(nodeId)
        reactivityNode?.value = value
    }

    private fun <V> createNode(
        type: ReactivityNode.Type<V>,
        initialValue: V?
    ): NodeId {
        val id = NodeId((nodes.size + 1).toLong(), viewRuntime)

        val reactivityNode: ReactivityNode<*> = ReactivityNode(id, initialValue, type)
        nodes[id] = reactivityNode

        if (type is ReactivityNode.Type.Signal) {
            rootNodes[id] = reactivityNode
        }

        return id
    }

    override fun <T : Any> createSignal(
        valueType: Class<T>,
        defaultValueProvider: ReceiverFunction<ViewRuntime, T?>
    ): Signal<T> {
        val id = createNode(object : ReactivityNode.Type.Signal<T> {}, with(defaultValueProvider) {viewRuntime.apply()})
        return SignalImpl(id, valueType.kotlin)
    }

    override fun <T : Any> createMemo(fn: Function<T?, T?>): Memo<T> {
        val reactivityNodeId = createNode(object : ReactivityNode.Type.Memo<T> {

            override fun computation(): AnyComputation<T?> = MemoState {
                val newValue = fn.apply(it)
                Pair(newValue, newValue != it)
            }

        }, null)

        return MemoImpl(reactivityNodeId, null)
    }

    override fun <S, T> createStore(
        storeProvider: ReceiverFunction<ViewRuntime, S>,
        supplier: ReceiverFunction<S, T>,
        consumer: ReceiverBiConsumer<S, T>
    ): Signal<T> {
        TODO("Not yet implemented!")
    }

    override fun <T> resourceSync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>> {
        TODO("Not yet implemented!")
    }

    override fun <I, T> resourceSync(
        input: Signal<I>,
        fetch: TriFunction<Platform, ViewRuntime, I, T>
    ): Signal<Optional<T>> {
        TODO("Not yet implemented!")
    }

    override fun <T> resourceAsync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>> {
        TODO("Not yet implemented!")
    }

    fun <T> createCustomEffect(additionalSignals: List<Signal<*>>, value: T?, effect: AnyComputation<T?>): Effect {
        val id = createNode(
            object : ReactivityNode.Type.Effect<T> {
                override fun computation(): AnyComputation<T?> = effect
            },
            value
        )
        val node = nodes[id]

        additionalSignals.forEach {
            val usedNode = nodes[(it as SignalImpl<*>).id()]
            if (usedNode != null) {
                node?.subscribe(usedNode)
            }
        }
        return EffectImpl(id)
    }

    override fun <T> createEffect(
        additionalSignals: List<Signal<*>>,
        effect: SignalableReceiverFunction<T?, T>
    ): Effect {
        return createCustomEffect(additionalSignals, null as T?, EffectState(effect))
    }
}
