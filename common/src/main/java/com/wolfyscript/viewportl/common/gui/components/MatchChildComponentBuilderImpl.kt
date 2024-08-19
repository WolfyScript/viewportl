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

package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.viewportl.gui.BuildContext
import com.wolfyscript.viewportl.gui.ViewRuntimeImpl
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.function.ReceiverFunction
import java.util.function.Supplier
import kotlin.reflect.KClass

class MatchChildComponentBuilderImpl(private val context: BuildContext) :
    com.wolfyscript.viewportl.gui.components.MatchChildComponentBuilder {

    private val matchers: MutableList<com.wolfyscript.viewportl.common.gui.components.MatchChildComponentBuilderImpl.Matcher<*>> = mutableListOf()

    inner class Matcher<V : Any>(
        val valueType: KClass<V>,
        val value: Supplier<V?>,
        val cases: MutableList<com.wolfyscript.viewportl.common.gui.components.MatchChildComponentBuilderImpl.Case<V>> = mutableListOf()
    ) {

        fun build(parent: com.wolfyscript.viewportl.gui.components.Component?) {
            val valueMemo = context.reactiveSource.createMemo(valueType.java) { value.get() }

            val runtime = context.runtime
            context.reactiveSource.createEffect {
                runtime as ViewRuntimeImpl
                val parentNodeId = (parent as? com.wolfyscript.viewportl.common.gui.components.AbstractComponentImpl<*>)?.nodeId ?: 0
                val value = valueMemo.get()
                val id = cases.find {
                    with(it.condition) {
                        value.apply()
                    }
                }?.builderConsumer?.let { builderConsumer ->
                    val builder = context.getOrCreateComponent(parent, null, _root_ide_package_.com.wolfyscript.viewportl.gui.components.ComponentGroup::class.java)
                    return@let with(builderConsumer) {
                        builder.consume()
                        builder.let {
                            val comp = it
                            comp.insert(runtime, parentNodeId)
                            comp.nodeId()
                        }
                    }
                } ?: -1

                context.reactiveSource.createCleanup {
                    val graph = runtime.modelGraph
                    val previousNode = graph.getNode(id)
                    val previousComponent = previousNode?.component
                    previousComponent?.remove(runtime, previousNode.id, parentNodeId)
                }
            }
        }
    }

    class Case<V>(val condition: ReceiverFunction<V?, Boolean>, val builderConsumer: ReceiverConsumer<com.wolfyscript.viewportl.gui.components.ComponentGroup>)

    override fun <V : Any> match(
        valueType: KClass<V>,
        value: Supplier<V?>,
        cases: ReceiverConsumer<com.wolfyscript.viewportl.gui.components.MatchChildComponentBuilder.Cases<V>>
    ) {
        val matcher = Matcher(valueType, value)
        matchers.add(matcher)
        val casesImpl = CasesImpl(matcher)
        with(cases) {
            casesImpl.consume()
        }
    }

    override fun buildMatchers(parent: _root_ide_package_.com.wolfyscript.viewportl.gui.components.Component?) {
        for (matcher in matchers) {
            matcher.build(parent)
        }
    }

    inner class CasesImpl<V : Any>(private val matcher: _root_ide_package_.com.wolfyscript.viewportl.common.gui.components.MatchChildComponentBuilderImpl.Matcher<V>) :
        _root_ide_package_.com.wolfyscript.viewportl.gui.components.MatchChildComponentBuilder.Cases<V> {

        override fun case(
            condition: ReceiverFunction<V?, Boolean>,
            builderConsumer: ReceiverConsumer<_root_ide_package_.com.wolfyscript.viewportl.gui.components.ComponentGroup>
        ) {
            matcher.cases.add(
                _root_ide_package_.com.wolfyscript.viewportl.common.gui.components.MatchChildComponentBuilderImpl.Case(
                    condition,
                    builderConsumer
                )
            )
        }

    }

}