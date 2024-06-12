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
package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.utilities.functions.ReceiverBiFunction
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.interaction.DragTransaction
import java.util.function.Consumer
import java.util.function.Supplier

interface StackInputSlot : Component {

    var value: ItemStack?

    fun value(fn: Supplier<ItemStack?>)

    var onValueChange: Consumer<ItemStack?>?

    var onClick: ReceiverConsumer<ClickTransaction>?

    var onDrag: ReceiverConsumer<DragTransaction>?

    var canPickUpStack : ReceiverBiFunction<ClickType, ItemStack, Boolean>?

    fun onClick(fn: ReceiverConsumer<ClickTransaction>) { onClick = fn }


}
