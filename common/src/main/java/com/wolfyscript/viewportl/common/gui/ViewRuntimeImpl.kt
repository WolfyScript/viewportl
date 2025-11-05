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

package com.wolfyscript.viewportl.common.gui

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.CoroutineScope
import java.util.*
import kotlin.coroutines.CoroutineContext

class ViewRuntimeImpl(
    override val viewportl: Viewportl,
    val parentCoroutineContext: CoroutineContext,
    override val window: Window,
    override var viewers: Set<UUID>,
    // Handlers that handle rendering and interaction
    override val renderer: Renderer<*>,
    override val interactionHandler: InteractionHandler<*>
) : ViewRuntime {

    // TODO: Coroutine Scope for this runtime
    // TODO: GUI runtimes are completely async, so need a way to securely communicate with main thread from Composable
    val coroutineScope = CoroutineScope(parentCoroutineContext)

    override val id: Long = NEXT_ID++

    // Build the components and init the rendering tree

    init {
        interactionHandler.init(this)
    }

    override fun dispose() {
        interactionHandler.dispose()
    }

    override fun open() {
        renderer.onWindowOpen(this, window)
        interactionHandler.onWindowOpen(window)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ViewRuntimeImpl
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    companion object {
        private var NEXT_ID = Long.MIN_VALUE
    }
}
