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

import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableSupplier
import com.wolfyscript.utilities.gui.functions.getNodeIds
import com.wolfyscript.utilities.gui.reactivity.*
import com.wolfyscript.utilities.gui.rendering.PropertyPosition

class ConditionalChildComponentBuilderImpl<T>(private val owner: T, private val context: BuildContext) :
    ConditionalChildComponentBuilder<T> {

    private var condition: SerializableSupplier<Boolean>? = null
    private var whenImpl: WhenImpl? = null
    private var elseImpl: ElseImpl? = null

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<T> {
        if (whenImpl == null) {
            whenImpl = WhenImpl()
        }
        this.condition = condition
        return whenImpl!!
    }

    fun build(parent: Component?) {
        val whenComponent = whenImpl?.build(parent) ?: return
        val elseComponent = elseImpl?.build(parent)

        if (condition == null) return
        val conditionMemo: Memo<Boolean> = context.reactiveSource.createMemo { condition!!.get() }

        val runtime = context.runtime

        val effect = context.reactiveSource.createEffect<Long> {
            runtime as ViewRuntimeImpl
            val graph = runtime.renderingGraph
            val previousNode = this?.let { graph.getNode(it) }

            val previousComponent = previousNode?.component
            if (previousComponent is Renderable) {
                previousComponent.remove(runtime, previousNode.id, 0)
            }

            val result = conditionMemo.get() ?: false
            when {
                result -> graph.addNode(whenComponent)
                elseComponent != null -> graph.addNode(elseComponent)
                else -> -1
            }
        }

        val conditionNode = context.reactiveSource.untypedNode((conditionMemo as MemoImpl).id)
        val effectNode = context.reactiveSource.untypedNode((effect as EffectImpl).id)

        condition?.getNodeIds()?.forEach {
            val node = context.reactiveSource.untypedNode(it)
            if (node != null) {
                conditionNode?.subscribe(node)
            }
        }
        if (conditionNode != null) {
            effectNode?.subscribe(conditionNode)
        }
    }

    inner class WhenImpl : ConditionalChildComponentBuilder.When<T> {

        private var componentBuilder: Long? = null

        override fun then(builderConsumer: ReceiverConsumer<ComponentClusterBuilder>): ConditionalChildComponentBuilder.Else<T> {
            val numericId = context.getOrCreateNumericId()
            val builderTypeInfo = ComponentUtil.getBuilderType(
                context.wolfyUtils,
                "internal_${numericId}",
                ComponentClusterBuilder::class.java
            )
            val builder: ComponentClusterBuilder =
                context.findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
                    val builderId = context.instantiateNewBuilder(numericId, PropertyPosition.static(), builderTypeInfo)
                    componentBuilder = builderId
                    context.getBuilder(builderId, builderTypeInfo.value)
                }
            with(builderConsumer) { builder.consume() }

            if (elseImpl == null) {
                elseImpl = ElseImpl()
            }
            return elseImpl!!
        }

        fun build(parent: Component?): Component? {
            return componentBuilder?.let {
                context.getBuilder(it)?.create(parent)
            }
        }

    }

    inner class ElseImpl : ConditionalChildComponentBuilder.Else<T> {

        private var componentBuilder: Long? = null

        override fun orElse(builderConsumer: ReceiverConsumer<ComponentClusterBuilder>): T {
            val numericId = context.getOrCreateNumericId()
            val builderTypeInfo = ComponentUtil.getBuilderType(
                context.wolfyUtils,
                "internal_${numericId}",
                ComponentClusterBuilder::class.java
            )
            val builder: ComponentClusterBuilder =
                context.findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
                    val builderId = context.instantiateNewBuilder(
                        numericId,
                        PropertyPosition.static(),
                        builderTypeInfo
                    )
                    componentBuilder = builderId
                    context.getBuilder(builderId, builderTypeInfo.value)
                }
            with(builderConsumer) { builder.consume() }
            return owner
        }

        override fun elseNone(): T = owner

        fun build(parent: Component?): Component? {
            return componentBuilder?.let {
                context.getBuilder(it)?.create(parent)
            }
        }

    }


}