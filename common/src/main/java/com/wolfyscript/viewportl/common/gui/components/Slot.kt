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

package com.wolfyscript.viewportl.common.gui.components

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.inject.Inject
import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.components.NativeComponentImplementation
import com.wolfyscript.viewportl.gui.components.SlotProperties
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.interaction.ClickType
import java.util.function.Consumer

internal fun setupSlot(properties: SlotProperties) {
    val runtime = properties.scope.runtime.into()
    val reactiveSource = runtime.reactiveSource

    val slot = SlotImpl(
        "",
        runtime.viewportl,
        properties.scope.parent?.component,
        onValueChange = properties.onValueChange,
        canPickUpStack = properties.canPickUpStack,
        value = properties.value()
    )
    properties.styles(slot.styles)

    val id = (properties.scope as ComponentScopeImpl).setComponent(slot)

    reactiveSource.createEffect {
        slot.value = properties.value()
    }

}

@NativeComponentImplementation(base = StackInputSlot::class)
@StaticNamespacedKey(key = "slot")
class SlotImpl @JsonCreator @Inject constructor(
    id: String,
    viewportl: Viewportl,
    parent: NativeComponent? = null,
    override var onValueChange: Consumer<ItemStack?>? = null,
    override var canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null,
    override var value: ItemStack? = null
) : AbstractNativeComponentImpl<StackInputSlot>(
    id, viewportl, parent
), StackInputSlot
