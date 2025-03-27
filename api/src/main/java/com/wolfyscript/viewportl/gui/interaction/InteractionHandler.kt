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
import com.wolfyscript.viewportl.gui.model.ModelChangeListener

/**
 * Handles the interaction of [Elements][com.wolfyscript.viewportl.gui.components.Element] present in the [Model][com.wolfyscript.viewportl.gui.model.ModelGraph] of a [ViewRuntime].
 * It is supplied to the [ViewRuntime] upon creation.
 *
 * For a given type of GUI each platform has its own [InteractionHandler] implementation (with possibly shared common parts)
 *
 * It can be assumed that when this ([Self]) is used for the runtime, then it has the same [InteractionHandler] type.
 * Though, the [Renderer][com.wolfyscript.viewportl.gui.rendering.Renderer] type is unknown!
 */
interface InteractionHandler<Self: InteractionHandler<Self>> : ModelChangeListener {

    fun init(runtime: ViewRuntime<*, Self>)

    fun dispose()

    fun onWindowOpen(window: Window)

}