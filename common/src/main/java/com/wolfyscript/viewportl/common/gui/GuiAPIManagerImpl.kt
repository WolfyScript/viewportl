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

import androidx.compose.runtime.Composable
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

class GuiAPIManagerImpl(private val viewportl: Viewportl) : GuiAPIManager {

    // TODO: Root Coroutine Scope for GUIs
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val entriesMap: BiMap<Key, Window> = HashBiMap.create()

    private val runtimes: Long2ObjectMap<ViewRuntime> = Long2ObjectOpenHashMap()
    private val cachedViewRuntimes: Multimap<Key, Long> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val viewRuntimesPerPlayer: Multimap<UUID, Long> = MultimapBuilder.hashKeys().hashSetValues().build()

    override fun getViewManagersFor(uuid: UUID): Stream<ViewRuntime> {
        return viewRuntimesPerPlayer[uuid].stream().map { runtimes[it] }
    }

    override fun getViewManagersFor(uuid: UUID, id: Key): Stream<ViewRuntime> {
        val ids = cachedViewRuntimes[id]
        return viewRuntimesPerPlayer[uuid].stream()
            .filter { ids.contains(it) }
            .map { runtimes[it] }
    }

    override fun clearFromCache(id: Key) {
        val runtimes = cachedViewRuntimes[id]
        cachedViewRuntimes.removeAll(runtimes)

        val playerEntries = viewRuntimesPerPlayer.entries().iterator()
        while (playerEntries.hasNext()) {
            val entry = playerEntries.next()
            if (runtimes.contains(entry.value)) {
                this.runtimes[entry.value].dispose()
                playerEntries.remove()
            }
        }

        cachedViewRuntimes.removeAll(id)
    }

    override fun registerGui(id: Key, content: @Composable () -> Unit) {
         entriesMap[id] = WindowImpl(id, viewportl = viewportl, content = content)
    }

    override fun createView(
        id: Key,
        content: @Composable (() -> Unit),
        viewers: Set<UUID>,
    ): ViewRuntime {
        val window = WindowImpl(id, viewportl = viewportl, content = content)
        val view = viewportl.guiFactory.createInventoryUIRuntime(viewportl, coroutineScope.coroutineContext, window, viewers)
        synchronized(runtimes) {
            runtimes.put(view.id, view)
        }
        synchronized(viewRuntimesPerPlayer) {
            for (viewer in viewers) {
                viewRuntimesPerPlayer.put(viewer, view.id)
            }
        }

        return view
    }

    override fun getGui(id: Key): Window? {
        return entriesMap[id]
    }

}
