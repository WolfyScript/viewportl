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

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.identifier.Key
import java.util.*
import java.util.stream.Stream

/**
 * Handles the general GUI API and acts as an entry point to the whole creation of [Router]s and [ViewRuntime]s.<br></br>
 * It stores all the registered [Router]s and allows to register new clusters via builders.<br></br>
 * Additionally, it stores the [ViewRuntime]s that handle the views for players.
 */
interface GuiAPIManager {

    /**
     * Registers a new window with the specified id.
     *
     * @param id The unique id of the window
     * @param windowConsumer The consumer that provides the new window
     */
    fun registerGui(id: Key, content: @Composable () -> Unit)

    /**
     * Gets the registered router with the specified id.<br></br>
     *
     * @param id The id of the router.
     * @return The registered router only if the id matches; otherwise empty Optional.
     */
    fun getGui(id: Key): Window?

    /**
     * Creates a new view for the specified viewers, with the specified GUI.
     *
     * @param viewers The viewers of this view.
     */
    fun createView(id: Key, content: @Composable () -> Unit, viewers: Set<UUID>) : ViewRuntime

    fun getViewManagersFor(uuid: UUID): Stream<ViewRuntime>

    fun getViewManagersFor(uuid: UUID, id: Key): Stream<ViewRuntime>

    fun clearFromCache(id: Key)
}
