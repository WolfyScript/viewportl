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

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.PlayerViewRuntime
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.ViewportlUIRuntimeManager
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class ViewportlUIRuntimeManagerImpl(private val viewportl: Viewportl) : ViewportlUIRuntimeManager, CoroutineScope {

    /*
     * TODO: Rework runtime system.
     *  - Allow each Runtime to have an owner, that is never removed upon closing the view
     *  - Allow others to join it and therefor see the same state
     *  - Allow entities to "own" a runtime (like NPCs) with persistent data stores
     *  - Create multi-state UIs, where state from other viewers can be used within the shared runtime
     *    e.i. common section in the UI is same for all viewers, while other sections display player specific state
     */

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    private val runtimes: Long2ObjectMap<UIRuntime> = Long2ObjectOpenHashMap()
    private val playerRuntimes: MutableMap<UUID, PlayerViewRuntimeImpl> = mutableMapOf()
    private val sharedUIRuntimes: MutableMap<Key, UIRuntime> = mutableMapOf()

    override fun createViewRuntime(
        id: Key,
        viewers: Set<UUID>,
        then: suspend (UIRuntime) -> Unit,
    ) {
        launch {
            val view: UIRuntime = if (viewers.size > 1) {
                val owner = viewers.first()
                val sharedView = sharedUIRuntimes[id] ?: viewportl.guiFactory.createUIRuntime(
                    viewportl,
                    coroutineContext,
                    owner
                )

                for (viewer in viewers) {
                    val playerRuntimes = playerRuntimes.getOrPut(viewer) { PlayerViewRuntimeImpl(viewer, viewportl) }
                    playerRuntimes.activeRuntime = sharedView
                    sharedView.joinViewer(viewer)
                }

                sharedView
            } else {
                val viewer = viewers.first()
                playerRuntimes.getOrPut(viewer) { PlayerViewRuntimeImpl(viewer, viewportl) }.getOwn(coroutineContext)
            }

            synchronized(runtimes) {
                runtimes.put(view.id, view)
            }

            then(view)
        }
    }

    override fun getPlayerRuntime(player: UUID): PlayerViewRuntime {
        return playerRuntimes.getOrPut(player) { PlayerViewRuntimeImpl(player, viewportl) }
    }

    override fun getViewRuntime(player: UUID): UIRuntime {
        return getPlayerRuntime(player).getOwn(coroutineContext)
    }

}

internal class PlayerViewRuntimeImpl(val player: UUID, val viewportl: Viewportl) : PlayerViewRuntime {

    override var activeRuntime: UIRuntime? = null

    private var usedCoroutineContext: CoroutineContext? = null
    private var ownRuntime: UIRuntime? = null

    override fun getOwn(coroutineContext: CoroutineContext): UIRuntime {
        if (ownRuntime == null || usedCoroutineContext != coroutineContext) {
            ownRuntime = viewportl.guiFactory.createUIRuntime(viewportl, coroutineContext, player)
            usedCoroutineContext = coroutineContext
        }
        return ownRuntime!!
    }

}
