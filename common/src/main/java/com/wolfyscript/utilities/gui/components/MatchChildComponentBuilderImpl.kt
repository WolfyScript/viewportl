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

package com.wolfyscript.utilities.gui.components

import com.wolfyscript.utilities.gui.BuildContext
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.Renderable
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import java.util.function.Supplier
import kotlin.reflect.KClass

class MatchChildComponentBuilderImpl(private val context: BuildContext) : MatchChildComponentBuilder {

    private val matchers: MutableList<Matcher<*>> = mutableListOf()

    inner class Matcher<V : Any>(
        val valueType: KClass<V>,
        val value: Supplier<V?>,
        val cases: MutableList<Case<V>> = mutableListOf()
    ) {

        fun build(parent: Component?) {
            val valueMemo = context.reactiveSource.createMemo(valueType.java) { value.get() }
            val cases = cases.mapNotNull { case ->
                context.getBuilder(case.builder)?.let {
                    Pair(case.condition, it.create(parent)!!)
                }
            }

            val runtime = context.runtime
            context.reactiveSource.createEffect<Long> {
                runtime as ViewRuntimeImpl
                val graph = runtime.renderingGraph
                val previousNode = this?.let { graph.getNode(it) }
                val previousComponent = previousNode?.component

                val parentNodeId = (parent as? AbstractComponentImpl)?.nodeId ?: 0

                if (previousComponent is Renderable) {
                    previousComponent.remove(runtime, previousNode.id, parentNodeId)
                }

                val value = valueMemo.get()

                return@createEffect cases.find {
                    with(it.first) {
                        value.apply()
                    }
                }?.second?.let {
                    if (it is Renderable) {
                        it.insert(runtime, parentNodeId)
                        return@createEffect it.nodeId()
                    }
                    return@createEffect -1
                } ?: -1
            }
        }
    }

    class Case<V>(val condition: ReceiverFunction<V?, Boolean>, val builder: Long)

    override fun <V : Any> match(
        valueType: KClass<V>,
        value: Supplier<V?>,
        cases: ReceiverConsumer<MatchChildComponentBuilder.Cases<V>>
    ) {
        val matcher = Matcher(valueType, value)
        matchers.add(matcher)
        val casesImpl = CasesImpl(matcher)
        with(cases) {
            casesImpl.consume()
        }
    }

    override fun buildMatchers(parent: Component?) {
        for (matcher in matchers) {
            matcher.build(parent)
        }
    }

    inner class CasesImpl<V : Any>(private val matcher: Matcher<V>) : MatchChildComponentBuilder.Cases<V> {

        override fun case(
            condition: ReceiverFunction<V?, Boolean>,
            builderConsumer: ReceiverConsumer<ComponentGroupBuilder>
        ) {
            var id = -1L
            val builder: ComponentGroupBuilder =
                context.getOrCreateComponentBuilder(null, ComponentGroupBuilder::class.java) {
                    id = it
                }
            with(builderConsumer) {
                builder.consume()
            }
            matcher.cases.add(Case(condition, id))
        }

    }


}