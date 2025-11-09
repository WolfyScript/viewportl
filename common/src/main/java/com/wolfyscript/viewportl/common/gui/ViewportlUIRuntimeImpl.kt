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

import androidx.compose.runtime.mutableStateMapOf
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.ViewportlUIRuntime
import com.wolfyscript.viewportl.gui.ViewRuntime
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
    private val cachedViewRuntimes: Multimap<Key, Long> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val viewRuntimesPerPlayer: MutableMap<UUID, Long> = mutableStateMapOf()

    fun clearFromCache(id: Key) {
        val runtimes = cachedViewRuntimes[id]
        cachedViewRuntimes.removeAll(runtimes)

        val playerEntries = viewRuntimesPerPlayer.entries.iterator()
        while (playerEntries.hasNext()) {
            val entry = playerEntries.next()
            if (runtimes.contains(entry.value)) {
                this.runtimes[entry.value].dispose()
                playerEntries.remove()
            }
        }

        cachedViewRuntimes.removeAll(id)
    }

    override fun createViewRuntime(
        id: Key,
        viewers: Set<UUID>,
        then: suspend (ViewRuntime) -> Unit,
    ) {
        // TODO: Reuse cached runtime or get rid of caching
        launch {
            val view = viewportl.guiFactory.createInventoryUIRuntime(viewportl, coroutineContext, viewers)
            synchronized(runtimes) {
                runtimes.put(view.id, view)
            }
            synchronized(viewRuntimesPerPlayer) {
                for (viewer in viewers) {
                    viewRuntimesPerPlayer[viewer] = view.id
                }
            }

            then(view)
        }
    }

    override fun getActiveRuntime(player: UUID): ViewRuntime? {
        return viewRuntimesPerPlayer[player]?.let { runtimes.get(it as Long) }
    }

}
