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

import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.compose.Node

/**
 * Renders the state of a composition
 */
interface Renderer<C: RenderContext> {

    /**
     * Whether the renderer is actively asking for new frames to be rendered.
     * When false, then the previous frame is kept and rendered as is.
     */
    var requestsNewFrames: Boolean

    fun onViewInit(runtime: UIRuntime, view: View)

    fun onViewChange(runtime: UIRuntime, view: View)

    fun render(view: View, node: Node)

}
