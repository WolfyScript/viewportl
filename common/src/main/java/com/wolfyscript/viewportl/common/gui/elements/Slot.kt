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

package com.wolfyscript.viewportl.common.gui.elements

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.inject.Inject
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.gui.elements.ElementImplementation
import com.wolfyscript.viewportl.gui.elements.SlotProperties
import com.wolfyscript.viewportl.gui.elements.StackInputSlot
import com.wolfyscript.viewportl.gui.interaction.ClickType

internal fun setupSlot(properties: SlotProperties): StackInputSlot {
    return SlotImpl(
        "",
        onValueChange = properties.onValueChange,
        canPickUpStack = properties.canPickUpStack,
    )
}

@ElementImplementation(base = StackInputSlot::class)
@StaticNamespacedKey(key = "slot")
class SlotImpl @JsonCreator @Inject constructor(
    id: String,
    override var onValueChange: ScafallItemStack.() -> Unit = { },
    override var canPickUpStack: ClickType.(ScafallItemStack) -> Boolean = { true },
    override var value: ScafallItemStack? = null
) : AbstractElementImpl<StackInputSlot>(id), StackInputSlot
