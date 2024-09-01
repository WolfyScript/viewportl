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
import com.wolfyscript.viewportl.common.gui.reactivity.ReactiveGraph
import com.wolfyscript.viewportl.common.gui.model.ModelGraphImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.ModelGraph
import com.wolfyscript.viewportl.gui.rendering.Renderer
import java.util.*
import java.util.function.Function

class ViewRuntimeImpl(
    override val viewportl: Viewportl,
    rootRouter: Function<ViewRuntime, Window>,
    override val viewers: Set<UUID>,
) : ViewRuntime {

    override val id: Long = NEXT_ID++

    // Create model & reactivity graphs
    override val model: ModelGraph = ModelGraphImpl(this)
    val reactiveSource: ReactiveGraph = ReactiveGraph(this)

    // Create platform specific handlers that handle rendering and interaction
    val renderer: Renderer<*> = viewportl.guiFactory.createRenderer(this)
    val interactionHandler: InteractionHandler = viewportl.guiFactory.createInteractionHandler(this)

    // Build the components and init the rendering tree
    val buildContext: BuildContext = BuildContext(this, reactiveSource, viewportl)
    private var currentRoot: Window? = rootRouter.apply(this)
    override val currentMenu: Window?
        get() = currentRoot

    private val history: Deque<Window> = ArrayDeque()

    init {
        model.registerListener(renderer)
        model.registerListener(interactionHandler)
    }

    override fun openNew() {
        openNew(*emptyArray())
    }

    override fun openNew(vararg path: String) {
        currentMenu?.apply {
            open(this)
        }
    }

    override fun open() {
        if (history.isEmpty()) {
            currentMenu?.close()
            openNew()
        } else {
            currentMenu?.let { open(it) }
        }
    }

    private fun open(window: Window) {
        setCurrentRoot(window)

        renderer.changeWindow(window)
        interactionHandler.init(window)

        viewportl.scafall.scheduler.syncTask(viewportl.scafall.corePlugin) {
            renderer.render()
            reactiveSource.owner()?.update()
        }
    }

    override fun openPrevious() {
        history.poll() // Remove active current menu
        val window = history.peek()
        currentMenu?.close()
        open(window)
    }

    fun setCurrentRoot(currentRoot: Window?) {
        this.currentRoot = currentRoot
    }

    fun getCurrentMenu(): Optional<Window> {
        return Optional.ofNullable(currentRoot)
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
