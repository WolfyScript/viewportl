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

import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Stream

/**
 * Handles the general GUI API and acts as an entry point to the whole creation of [Router]s and [ViewRuntime]s.<br></br>
 * It stores all the registered [Router]s and allows to register new clusters via builders.<br></br>
 * Additionally, it stores the [ViewRuntime]s that handle the views for players.
 */
interface GuiAPIManager {

    val registeredGuis : Set<String>

    /**
     * Registers a new window with the specified id.<br></br>
     * The consumer provides the newly constructed [Window], which can then be configured.<br></br>
     *
     * @param key The unique id of the window
     * @param windowConsumer The consumer that provides the new window
     */
    fun registerGui(key: String, windowConsumer: WindowScope.() -> Unit)

    /**
     * Gets the registered router with the specified id.<br></br>
     *
     * @param id The id of the router.
     * @return The registered router only if the id matches; otherwise empty Optional.
     */
    fun getGui(id: String): Optional<Function<ViewRuntime, Window>>

    /**
     * Creates a new view for the specified viewers, with the specified GUI.
     *
     *
     * The view is build async, so care should be taken to not access any main-thread objects (e.g. Entities, World, etc.).<br></br>
     * When such data is required inside the GUI use [ReactiveSource.resourceSync]!
     *
     *
     *
     * The callback is run right after the creation (or retrieval) of the view manager.<br></br>
     * **That means the callback may be ASYNC!**
     *
     *
     * @param guiId The id of the gui.
     * @param callback The callback, that is run right after the view manager has been created. **May be Async!**
     * @param viewers The viewers of this view.
     */
    fun createViewAndThen(guiId: String, callback: Consumer<ViewRuntime>, vararg viewers: UUID)

    /**
     * Creates (or gets the existing ViewManager) and opens the entry menu right after the creation of the view.
     *
     * @param guiID The id of the gui.
     * @param viewers The viewers of this view.
     */
    fun createViewAndOpen(guiID: String, vararg viewers: UUID)

    fun getViewManagersFor(uuid: UUID): Stream<ViewRuntime>

    fun getViewManagersFor(uuid: UUID, guiID: String): Stream<ViewRuntime>

    fun clearFromCache(guiId: String)
}
