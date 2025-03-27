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
import com.wolfyscript.viewportl.common.gui.model.ModelGraphImpl
import com.wolfyscript.viewportl.common.gui.reactivity.ReactiveGraph
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.ModelGraph
import com.wolfyscript.viewportl.gui.rendering.Renderer
import java.util.*
import java.util.function.Function

class ViewRuntimeImpl<R: Renderer<R,*>, I: InteractionHandler<I>>(
    override val viewportl: Viewportl,
    windowFactory: Function<ViewRuntime<*,*>, Window>,
    override val viewers: Set<UUID>,
    override val model: ModelGraph = ModelGraphImpl(),
    // Handlers that handle rendering and interaction
    override val renderer: R,
    override val interactionHandler: I
) : ViewRuntime<R, I> {

    override val id: Long = NEXT_ID++

    // Create model & reactivity graphs
    override val reactiveSource: ReactiveGraph = ReactiveGraph(this)

    // Build the components and init the rendering tree
    private var currentWindow: Window? = windowFactory.apply(this)
    override val window: Window?
        get() = currentWindow

    private val history: Deque<Window> = ArrayDeque()

    init {
        model.registerListener(renderer)
        model.registerListener(interactionHandler)

        renderer.init(this)
        interactionHandler.init(this)
    }

    override fun dispose() {
        reactiveSource.exit()
        interactionHandler.dispose()
    }

    override fun openNew() {
        openNew(*emptyArray())
    }

    override fun openNew(vararg path: String) {
        window?.apply {
            open(this)
        }
    }

    override fun open() {
        if (history.isEmpty()) {
            window?.close()
            openNew()
        } else {
            window?.let { open(it) }
        }
    }

    private fun open(window: Window) {
        currentWindow = window

        renderer.onWindowOpen(window)
        interactionHandler.onWindowOpen(window)

        viewportl.scafall.scheduler.syncTask(viewportl.scafall.corePlugin) {
            renderer.render()
        }
    }

    override fun openPrevious() {
        history.poll() // Remove active current menu
        val window = history.peek()
        this.window?.close()
        open(window)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ViewRuntimeImpl<*,*>
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    companion object {
        private var NEXT_ID = Long.MIN_VALUE
    }
}
