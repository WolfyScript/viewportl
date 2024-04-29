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
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.reactivity.Memo
import com.wolfyscript.utilities.gui.reactivity.createMemo
import java.util.function.Supplier

class ConditionalChildComponentBuilderImpl(private val context: BuildContext) : ConditionalChildComponentBuilder {

    private val conditionals: MutableList<Conditional> = mutableListOf()

    data class Conditional(
        val condition: Supplier<Boolean>,
        var whenImpl: WhenImpl? = null,
        var elseImpl: ElseImpl? = null
    )

    override fun whenever(condition: Supplier<Boolean>): ConditionalChildComponentBuilder.When {
        val conditional = Conditional(condition)
        conditionals.add(conditional)
        return WhenImpl(conditional)
    }

    override fun buildConditionals(parent: Component?) {
        for (conditional in conditionals) {
            val conditionMemo: Memo<Boolean> = context.reactiveSource.createMemo { conditional.condition.get() }
            val runtime = context.runtime
            context.reactiveSource.createEffect<Unit> {
                runtime as ViewRuntimeImpl
                val parentNodeId = (parent as? AbstractComponentImpl<*>)?.nodeId ?: 0

                val result = conditionMemo.get() ?: false
                val id = when {
                    result -> {
                        conditional.whenImpl?.build(parent)?.let {
                            it.insert(runtime, parentNodeId)
                            it.nodeId()
                        } ?: -1
                    }

                    conditional.elseImpl != null -> {
                        conditional.elseImpl?.build(parent)?.let {
                            it.insert(runtime, parentNodeId)
                            it.nodeId()
                        } ?: -1
                    }

                    else -> -1
                }

                // Clean previous component before update and when disposed
                context.reactiveSource.createCleanup {
                    val graph = runtime.renderingGraph
                    val previousNode = graph.getNode(id)
                    val previousComponent = previousNode?.component
                    previousComponent?.remove(runtime, previousNode.id, parentNodeId)
                }
            }
        }
    }

    inner class WhenImpl(private val conditional: Conditional) : ConditionalChildComponentBuilder.When {

        private var builderConsumer: ReceiverConsumer<ComponentGroup>? = null

        override fun then(builderConsumer: ReceiverConsumer<ComponentGroup>): ConditionalChildComponentBuilder.Else {
            this.builderConsumer = builderConsumer
            conditional.whenImpl = this
            return ElseImpl(conditional)
        }

        fun build(parent: Component?): Component? {
            val builder = context.getOrCreateComponent(null, ComponentGroup::class.java)
            return builderConsumer?.let {
                with(builderConsumer!!) { builder.consume() }
                builder
            }
        }

    }

    inner class ElseImpl(private val conditional: Conditional) : ConditionalChildComponentBuilder.Else {

        private var builderConsumer: ReceiverConsumer<ComponentGroup>? = null

        override fun orElse(builderConsumer: ReceiverConsumer<ComponentGroup>) {
            this.builderConsumer = builderConsumer
            conditional.elseImpl = this
        }

        fun build(parent: Component?): Component? {
            val builder = context.getOrCreateComponent(null, ComponentGroup::class.java)
            return builderConsumer?.let {
                with(builderConsumer!!) { builder.consume() }
                builder
            }
        }

    }

}