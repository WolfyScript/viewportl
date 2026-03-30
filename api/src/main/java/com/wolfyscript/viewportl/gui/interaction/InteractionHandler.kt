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

package com.wolfyscript.viewportl.gui.interaction

import com.wolfyscript.scafall.wrappers.ScafallPlayer
import com.wolfyscript.viewportl.runtime.UIRuntime
import com.wolfyscript.viewportl.runtime.View
import com.wolfyscript.viewportl.ui.modifier.SlotInputModifierNode
import java.util.UUID

/**
 * Handles the interaction of modifier e.g. [com.wolfyscript.viewportl.ui.modifier.clickable].
 * It is supplied to the [UIRuntime] upon creation.
 *
 * For a given type of GUI each platform has its own [InteractionHandler] implementation
 */
interface InteractionHandler<C: InteractionContext> {

    /**
     * Whether this handler is currently handling an interaction.
     */
    val isBusy: Boolean

    fun init(runtime: UIRuntime)

    fun dispose()

    fun onViewOpened(view: View)

    fun onClick(context: C)

    fun onDrag(context: C)

    fun findSlotInputAt(viewer: UUID, slotIndex: Int): SlotInputModifierNode?

    fun clicked(viewer: UUID, slotIndex: Int)

}