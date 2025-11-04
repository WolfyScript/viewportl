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
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Stream

class GuiAPIManagerImpl(private val viewportl: Viewportl) : GuiAPIManager {

    // TODO: Root Coroutine Scope for GUIs
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val entriesMap: BiMap<Key, Function<ViewRuntime, Window>> = HashBiMap.create()

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
        registerGui(id) { runtime ->
            val window: Window = WindowImpl(id, 54, null, viewportl, content)

            window
        }
    }

    override fun createViewAndThen(id: Key, callback: Consumer<ViewRuntime>, vararg viewers: UUID) {
        getGui(id).ifPresent { constructor ->
            val viewerSet = mutableSetOf(*viewers)
            val viewManagersForID = cachedViewRuntimes[id]
            val runtime = viewManagersForID.map { runtimes[it] }.firstOrNull { it.viewers == viewerSet }
            if (runtime != null) {
                callback.accept(runtime)
            } else {
                // Construct the new view manager async, so it doesn't affect the main thread!
                viewportl.scafall.scheduler.asyncTask(viewportl.scafall.modInfo) {
                    val viewManager = viewportl.guiFactory.createInventoryUIRuntime(viewportl, constructor, viewerSet)
                    synchronized(runtimes) {
                        viewManagersForID.add(viewManager.id)
                        runtimes.put(viewManager.id, viewManager)
                    }
                    synchronized(viewRuntimesPerPlayer) {
                        for (viewer in viewerSet) {
                            viewRuntimesPerPlayer.put(viewer, viewManager.id)
                        }
                    }
                    callback.accept(viewManager)
                }
            }
        }
    }

    private fun registerGui(id: Key, constructor: Function<ViewRuntime, Window>) {
        entriesMap[id] = constructor
    }

    override fun createViewAndOpen(id: Key, vararg viewers: UUID) {
        createViewAndThen(id, { it.open() }, *viewers)
    }

    override fun getGui(id: Key): Optional<Function<ViewRuntime, Window>> {
        return Optional.ofNullable(
            entriesMap[id]
        )
    }

}
