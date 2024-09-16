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

package com.wolfyscript.viewportl.spigot.gui.inventoryui.interaction

import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.interaction.ClickType

class ClickTransactionImpl(
    override val clickType: ClickType,
    override val slot: Int,
    override val shift: Boolean,
    override val primary: Boolean,
    override val secondary: Boolean,
    override val hotbarIndex: Int,
) : ClickTransaction {

    override val cursorStack : ItemStack? = null
    override val currentStack : ItemStack? = null

}