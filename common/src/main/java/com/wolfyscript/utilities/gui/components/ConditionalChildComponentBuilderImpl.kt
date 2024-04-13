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
import com.wolfyscript.utilities.gui.reactivity.Memo
import com.wolfyscript.utilities.gui.reactivity.createMemo
import java.util.function.Supplier

class ConditionalChildComponentBuilderImpl(private val context: BuildContext) : ConditionalChildComponentBuilder {

    private val conditionals: MutableList<Conditional> = mutableListOf()

    data class Conditional(val condition: Supplier<Boolean>, var whenImpl: WhenImpl? = null, var elseImpl: ElseImpl? = null)

    override fun whenever(condition: Supplier<Boolean>): ConditionalChildComponentBuilder.When {
        val conditional = Conditional(condition)
        conditionals.add(conditional)
        return WhenImpl(conditional)
    }

    override fun buildConditionals(parent: Component?) {
        for (conditional in conditionals) {
            val whenComponent = conditional.whenImpl?.build(parent) ?: return
            val elseComponent = conditional.elseImpl?.build(parent)
            val conditionMemo: Memo<Boolean> = context.reactiveSource.createMemo { conditional.condition.get() }
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
                val result = conditionMemo.get() ?: false
                when {
                    result -> {
                        if (whenComponent is Renderable) {
                            whenComponent.insert(runtime, parentNodeId)
                        }
                        whenComponent.nodeId()
                    }

                    elseComponent != null -> {
                        if (elseComponent is Renderable) {
                            elseComponent.insert(runtime, parentNodeId)
                        }
                        elseComponent.nodeId()
                    }

                    else -> -1
                }
            }
        }
    }

    inner class WhenImpl(private val conditional: Conditional) : ConditionalChildComponentBuilder.When {

        private var componentBuilder: Long? = null

        override fun then(builderConsumer: ReceiverConsumer<ComponentGroupBuilder>): ConditionalChildComponentBuilder.Else {
            val builder: ComponentGroupBuilder = context.getOrCreateComponentBuilder(null, ComponentGroupBuilder::class.java) {
                componentBuilder = it
            }
            with(builderConsumer) { builder.consume() }
            conditional.whenImpl = this
            return ElseImpl(conditional)
        }

        fun build(parent: Component?): Component? {
            return componentBuilder?.let {
                context.getBuilder(it)?.create(parent)
            }
        }

    }

    inner class ElseImpl(private val conditional: Conditional) : ConditionalChildComponentBuilder.Else {

        private var componentBuilder: Long? = null

        override fun orElse(builderConsumer: ReceiverConsumer<ComponentGroupBuilder>) {
            val builder: ComponentGroupBuilder = context.getOrCreateComponentBuilder(null, ComponentGroupBuilder::class.java) {
                componentBuilder = it
            }
            with(builderConsumer) { builder.consume() }
            conditional.elseImpl = this
        }

        fun build(parent: Component?): Component? {
            return componentBuilder?.let {
                context.getBuilder(it)?.create(parent)
            }
        }

    }


}