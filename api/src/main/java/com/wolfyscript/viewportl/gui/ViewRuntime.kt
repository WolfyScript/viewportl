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
package com.wolfyscript.viewportl.gui

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.ModelGraph
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource
import com.wolfyscript.viewportl.gui.rendering.Renderer
import java.util.*

/**
 * The [ViewRuntime], as the name suggests, manages a view of the GUI for one or more players.<br></br>
 * It contains the custom Data object that stores all the required data of this view.<br></br>
 *
 * The view is immutable, so you need to create a new view each time you need to add a viewer or change the root.
 *
 */
interface ViewRuntime {

    val id: Long

    /**
     * Opens a new menu under the specific path.
     * When the component at the specified path cannot be rendered (is not a window) it'll use the entry of that component.
     *
     * @param path The path to the menu to open.
     */
    fun openNew(vararg path: String)

    /**
     * Opens the entry menu of this root cluster.
     */
    fun openNew()

    /**
     * Opens the currently active menu without updating the history.<br></br>
     * In case there is no active menu it opens the entry of the root cluster.
     */
    fun open()

    /**
     * Goes back to the previously opened menu and opens it.
     */
    fun openPrevious()

    fun dispose()

    /**
     * Gets the currently active menu.
     *
     * @return The currently active menu.
     */
    val window: Window?

    /**
     * Gets the viewers that are handled by this view manager.
     * When using these UUIDS, make sure the associated player is actually online!
     *
     * @return A Set of the viewers, that are handled by this manager.
     */
    val viewers: Set<UUID>

    val viewportl: Viewportl

    val model: ModelGraph

    val reactiveSource: ReactiveSource

    val renderer: Renderer<*>

    val interactionHandler: InteractionHandler<*>

}
