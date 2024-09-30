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
package com.wolfyscript.viewportl.gui.rendering

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.model.ModelChangeListener
import net.kyori.adventure.text.Component

/**
 * Renders [NativeComponents][com.wolfyscript.viewportl.gui.components.NativeComponent] present in the [Model][com.wolfyscript.viewportl.gui.model.ModelGraph] of a [ViewRuntime].
 * It is supplied to the [ViewRuntime] upon creation.
 *
 * For a given type of gui each platform has its own [Renderer] implementation (with possibly shared common parts)
 *
 *
 * It can be assumed that when this ([Self]) is used for the runtime, then it has the same [Renderer] type.
 * Though, the [InteractionHandler][com.wolfyscript.viewportl.gui.interaction.InteractionHandler] type is unknown!
 */
interface Renderer<Self: Renderer<Self, C>, C: RenderContext> : ModelChangeListener {

    val runtime: ViewRuntime<Self, *>

    fun init(runtime: ViewRuntime<Self, *>)

    fun onWindowOpen(window: Window)

    fun render()

    fun updateTitle(component: Component?)

}
