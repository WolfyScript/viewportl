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

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window

/**
 * Handles the interaction of [Elements][com.wolfyscript.viewportl.gui.elements.Element].
 * It is supplied to the [ViewRuntime] upon creation.
 *
 * For a given type of GUI each platform has its own [InteractionHandler] implementation (with possibly shared common parts)
 *
 */
interface InteractionHandler<C: InteractionContext> {

    /**
     * Whether this handler is currently handling an interaction.
     */
    val isBusy: Boolean

    fun init(runtime: ViewRuntime)

    fun dispose()

    fun onWindowOpen(window: Window)

    fun onClick(context: C)

    fun onDrag(context: C)

}