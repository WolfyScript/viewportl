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
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.ViewportlUIRuntime
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class ViewportlUIRuntimeImpl(private val viewportl: Viewportl) : ViewportlUIRuntime, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    private val runtimes: Long2ObjectMap<ViewRuntime> = Long2ObjectOpenHashMap()
    private val viewRuntimesPerPlayer: MutableMap<UUID, PlayerViewRuntimes> = mutableMapOf()
    private val sharedViewRuntimes: MutableMap<Key, ViewRuntime> = mutableMapOf()

    override fun createViewRuntime(
        id: Key,
        viewers: Set<UUID>,
        then: suspend (ViewRuntime) -> Unit,
    ) {
        launch {
            val view: ViewRuntime = if (viewers.size > 1) {
                val sharedView = sharedViewRuntimes[id] ?: viewportl.guiFactory.createInventoryUIRuntime(
                    viewportl,
                    coroutineContext,
                    viewers
                )

                for (viewer in viewers) {
                    val playerRuntimes = viewRuntimesPerPlayer.getOrPut(viewer) { PlayerViewRuntimes() }
                    playerRuntimes[id] = sharedView
                }

                sharedView
            } else {
                viewRuntimesPerPlayer.getOrPut(viewers.first()) { PlayerViewRuntimes() }.let {
                    val existing = it[id]
                    if (existing != null) {
                        existing.joinViewer(viewers.first())
                        return@let existing
                    }
                    val view = viewportl.guiFactory.createInventoryUIRuntime(viewportl, coroutineContext, viewers)
                    it[id] = view
                    view
                }
            }

            synchronized(runtimes) {
                runtimes.put(view.id, view)
            }

            then(view)
        }
    }

    override fun getActiveRuntime(player: UUID): ViewRuntime? {
        return viewRuntimesPerPlayer[player]?.let { runtimes.get(it as Long) }
    }

}

internal class PlayerViewRuntimes() {

    private val runtimes: MutableMap<Key, ViewRuntime> = mutableMapOf()

    operator fun get(id: Key): ViewRuntime? = runtimes[id]

    operator fun set(id: Key, runtime: ViewRuntime) {
        runtimes[id] = runtime
    }

}
