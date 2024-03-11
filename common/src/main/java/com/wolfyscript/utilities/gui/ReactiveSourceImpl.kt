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
package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.gui.functions.SignalableReceiverFunction
import com.wolfyscript.utilities.gui.reactivity.*
import com.wolfyscript.utilities.gui.signal.Signal
import com.wolfyscript.utilities.platform.Platform
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.apache.commons.lang3.function.TriFunction
import java.util.*
import java.util.function.BiFunction
import kotlin.collections.ArrayList

class ReactiveSourceImpl(private val viewRuntime: ViewRuntimeImpl) : ReactiveSource {

    private val allNodes: MutableMap<NodeId, Node<*>> = Object2ObjectOpenHashMap()
    private val rootNodes: MutableMap<NodeId, Node<*>> = Object2ObjectOpenHashMap()
    private val nodeSubscribers: MutableMap<NodeId, MutableSet<NodeId>> = Object2ObjectOpenHashMap()
    private val nodeSources: MutableMap<NodeId, MutableSet<NodeId>> = Object2ObjectOpenHashMap()
    private val pendingEffects: MutableList<NodeId> = ArrayList()

    fun markDirty(node: NodeId) {
        val currentNode = allNodes[node] ?: return

        currentNode.mark(Node.State.DIRTY)

        val children = nodeSubscribers[node]!!

        /**
         * A depth-first DAG (Direct Acyclic Graph) to mark all child nodes as CHECK recursively.
         * If a node is already DIRTY then we mark it as visited DIRTY_MARKED.
         *
         * We do this like Leptos here. Instead of pushing each and every node onto the stack, we push the iterators of child nodes.
         */
        val stack : ArrayDeque<Iterator<NodeId>> = ArrayDeque(children.size)
        stack.push(children.iterator())

        while (stack.isNotEmpty()) {
            val childIterator = stack.poll()

            val result = fun() : Pair<IterResult, Iterator<NodeId>?> {
                if (!childIterator.hasNext()) {
                    return Pair(IterResult.EMPTY, null)
                }
                var child = childIterator.next()

                while (allNodes[child] != null) {
                    val childNode = allNodes[child]!!

                    if (childNode.state() == Node.State.CHECK || childNode.state() == Node.State.DIRTY_MARKED) {
                        return Pair(IterResult.CONTINUE, null)
                    }

                    // TODO: Maybe move to an extra function
                    if (Node.State.CHECK > childNode.state()) {
                        childNode.mark(Node.State.CHECK)
                    }

                    if (childNode.type is Node.Type.Effect) {
                        pendingEffects.add(child)
                    }

                    if (childNode.state() == Node.State.DIRTY) {
                        childNode.mark(Node.State.DIRTY_MARKED)
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
            }.invoke()

            when(result.first) {
                IterResult.CONTINUE -> continue
                IterResult.NEW -> result.second?.let { stack.push(it) }
                IterResult.EMPTY -> stack.pop()
            }

        }

    }

    private fun mark(nodeId: NodeId, node: Node<*>, level: Node.State) {



    }

    enum class IterResult {
        CONTINUE,
        NEW,
        EMPTY

    }

    fun untypedNode(id: NodeId): Node<*>? {
        return allNodes[id]
    }

    inline fun <reified V: Node<*>> node(id: NodeId): V? {
        val untyped = untypedNode(id) ?: return null
        if (untyped !is V) {
            throw IllegalStateException("Node ($id) cannot be converted to ${V::class}!")
        }
        return untyped
    }

    private fun <V> createNode(
        type: Node.Type,
        initialValue: V
    ): NodeId {
        val id = NodeId((allNodes.size + 1).toLong(), viewRuntime)

        val node: Node<*> = Node(id, initialValue, type)
        allNodes[id] = node

        if (type is Node.Type.Signal) {
            rootNodes[id] = node
        }

        return id
    }

    override fun <T> createSignal(defaultValue: T): Signal<T> {
        val id = createNode(object : Node.Type.Signal {}, defaultValue)
        return SignalImpl(id, defaultValue)
    }

    override fun <T> createSignal(defaultValueProvider: ReceiverFunction<ViewRuntime, T>): Signal<T> {
        return createSignal(with(defaultValueProvider) { viewRuntime.apply() })
    }

    override fun <S, T> createStore(
        storeProvider: ReceiverFunction<ViewRuntime, S>,
        getter: ReceiverFunction<S, T>,
        setter: ReceiverBiConsumer<S, T>
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

    fun <T> createCustomEffect(additionalSignals: List<Signal<*>>, value: T?, effect: AnyComputation<T>): Effect {
        val id = createNode(
            object : Node.Type.Effect {
                override fun computation(): AnyComputation<*> = effect
            },
            value
        )
        val node = allNodes[id]

        additionalSignals.forEach {
            val usedNode = allNodes[(it as SignalImpl<*>).id()]
            if (usedNode != null) {
                node?.subscribe(usedNode)
            }
        }
        return EffectImpl(id)
    }

    override fun <T> createEffect(additionalSignals: List<Signal<*>>, effect: SignalableReceiverFunction<T?, T>): Effect {
        return createCustomEffect(additionalSignals, null as T?, EffectState(effect))
    }
}
