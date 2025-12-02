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
 * The [UIRuntime], manages the [View(s)][View] of a UI Interface for one or more players.
 *
 * It keeps track of the state across configuration changes (closing and reopening UIs),
 * provides the timings for UI updates and rendering, and manages the viewers.
 */
interface UIRuntime : CoroutineScope {

    val id: Long

    fun setNewView(
        id: Key,
        size: Int = 54,
        type: WindowType = WindowType.CUSTOM,
        content: @Composable () -> Unit,
    )

    fun openView()

    fun joinViewer(uuid: UUID)

    fun leaveViewer(uuid: UUID)

    fun dispose()

    /**
     * Gets the currently active menu.
     *
     * @return The currently active menu.
     */
    val view: View?

    val owner: UUID

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
