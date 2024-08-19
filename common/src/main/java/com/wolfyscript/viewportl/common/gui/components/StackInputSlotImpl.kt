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

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Component
import com.wolfyscript.viewportl.gui.components.ComponentImplementation
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.interaction.DragTransaction
import java.util.function.Consumer
import java.util.function.Supplier
import javax.annotation.Nullable

@ComponentImplementation(base = StackInputSlot::class)
@StaticNamespacedKey(key = "stack_input_slot")
class StackInputSlotImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: Viewportl,
    @JacksonInject("context") private val context: BuildContext,
    @Nullable @JacksonInject("parent") parent: Component? = null,
) : AbstractComponentImpl<StackInputSlot>(
    id, wolfyUtils, parent
), StackInputSlot {

    override var onValueChange: Consumer<ItemStack?>? = null
    override var onClick: ReceiverConsumer<ClickTransaction>? = null
    override var onDrag: ReceiverConsumer<DragTransaction>? = null
    override var canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null
    override var value: ItemStack? = null
    private var valueFn: Supplier<ItemStack?>? = null

    override fun value(fn: Supplier<ItemStack?>) {
        valueFn = fn
    }

    override fun width(): Int {
        return 1
    }

    override fun height(): Int {
        return 1
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        (runtime as ViewRuntimeImpl).modelGraph.removeNode(nodeId)
    }

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        runtime as ViewRuntimeImpl

        if (valueFn != null) {
            runtime.reactiveSource.createEffect {
                value = valueFn?.get()
            }
        }

        val id = runtime.modelGraph.addNode(this)
        runtime.modelGraph.insertNodeAsChildOf(id, parentNode)
    }

}
