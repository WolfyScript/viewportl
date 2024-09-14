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

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.interaction.DragInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.DragTransaction

class InventoryButtonInteractionHandler : SpigotComponentInteractionHandler<Button> {

    private fun playSound(runtime: ViewRuntime<*,*>, component: Button) {
        component.sound?.let { sound ->
            runtime.viewers.forEach {
                runtime.viewportl.scafall.adventure.player(it).playSound(sound)
            }
        }
    }

    override fun onClick(
        runtime: ViewRuntime<*,*>,
        component: Button,
        details: ClickInteractionDetails,
        transaction: ClickTransaction
    ) {
        playSound(runtime, component)

        component.onClick?.let { click ->
            transaction.click()
        }

        details.invalidate() // Never allow to validate it!
    }

    override fun onDrag(
        runtime: ViewRuntime<*,*>,
        component: Button,
        details: DragInteractionDetails,
        transaction: DragTransaction
    ) {
        details.invalidate()
    }

}