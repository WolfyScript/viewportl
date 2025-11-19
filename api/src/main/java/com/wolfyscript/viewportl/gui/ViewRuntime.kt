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
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.DataStoreMap
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.CoroutineScope
import java.util.*

/**
 * The [ViewRuntime], as the name suggests, manages a view of the GUI for one or more players.<br></br>
 * It contains the custom Data object that stores all the required data of this view.<br></br>
 *
 */
interface ViewRuntime : CoroutineScope {

    val id: Long

    fun createNewWindow(
        id: Key,
        size: Int = 54,
        type: WindowType = WindowType.CUSTOM,
        content: @Composable () -> Unit,
    )

    /**
     * Opens the currently active menu without updating the history.<br></br>
     * In case there is no active menu it opens the entry of the root cluster.
     */
    suspend fun open()

    fun joinViewer(uuid: UUID)

    fun leaveViewer(uuid: UUID)

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

    val renderer: Renderer<*>

    val storeOwner: DataStoreMap

    val interactionHandler: InteractionHandler<*>

}
