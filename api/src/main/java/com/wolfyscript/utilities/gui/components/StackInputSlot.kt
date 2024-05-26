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

import com.wolfyscript.utilities.functions.ReceiverBiFunction
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.interaction.ClickInteractionDetails
import com.wolfyscript.utilities.gui.interaction.ClickType
import com.wolfyscript.utilities.platform.adapters.ItemStack
import java.util.function.Consumer

interface StackInputSlot : Component {

    var value: ItemStack?

    var onValueChange: Consumer<ItemStack?>?

    var onClick: ReceiverConsumer<ClickInteractionDetails>?

    var acceptStack : ReceiverBiFunction<ClickType, ItemStack, Boolean>?

    var canPickUpStack : ReceiverBiFunction<ClickType, ItemStack, Boolean>?

    fun onClick(fn: ReceiverConsumer<ClickInteractionDetails>) { onClick = fn }

    fun acceptStack(fn: ReceiverBiFunction<ClickType, ItemStack, Boolean>) { acceptStack = fn }

}
