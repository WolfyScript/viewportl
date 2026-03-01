package com.wolfyscript.viewportl.runtime

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.runtime.View
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.StoreOwner
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.CoroutineScope
import java.util.UUID

/**
 * The [UIRuntime], manages the [View(s)][View] of a UI Interface for one or more players.
 *
 * It keeps track of the state across configuration changes (closing and reopening UIs),
 * provides the timings for UI updates and rendering, and manages the viewers.
 */
interface UIRuntime : CoroutineScope, StoreOwner {

    val id: Long

    fun setContent(
        content: @Composable () -> Unit,
    )

    fun openView()

    fun joinViewer(uuid: UUID)

    fun leaveViewer(uuid: UUID)

    fun dispose()

    val views: Map<UUID, View>

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

    val interactionHandler: InteractionHandler<*>

}