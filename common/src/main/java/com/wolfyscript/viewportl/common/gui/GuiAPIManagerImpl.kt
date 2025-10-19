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
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Stream

class GuiAPIManagerImpl(private val viewportl: Viewportl) : GuiAPIManager {
    private val entriesMap: BiMap<String, Function<ViewRuntime, Window>> = HashBiMap.create()

    private val runtimes: Long2ObjectMap<ViewRuntime> = Long2ObjectOpenHashMap()
    private val cachedViewRuntimes: Multimap<String, Long> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val viewRuntimesPerPlayer: Multimap<UUID, Long> = MultimapBuilder.hashKeys().hashSetValues().build()

    override fun getViewManagersFor(uuid: UUID): Stream<ViewRuntime> {
        return viewRuntimesPerPlayer[uuid].stream().map { runtimes[it] }
    }

    override fun getViewManagersFor(uuid: UUID, guiID: String): Stream<ViewRuntime> {
        val ids = cachedViewRuntimes[guiID]
        return viewRuntimesPerPlayer[uuid].stream()
            .filter { ids.contains(it) }
            .map { runtimes[it] }
    }

    override fun clearFromCache(guiId: String) {
        val runtimes = cachedViewRuntimes[guiId]
        cachedViewRuntimes.removeAll(runtimes)

        val playerEntries = viewRuntimesPerPlayer.entries().iterator()
        while (playerEntries.hasNext()) {
            val entry = playerEntries.next()
            if (runtimes.contains(entry.value)) {
                this.runtimes[entry.value].dispose()
                playerEntries.remove()
            }
        }

        cachedViewRuntimes.removeAll(guiId)
    }

    override val registeredGuis: Set<String>
        get() = entriesMap.keys

    override fun registerGui(key: String, content: @Composable () -> Unit) {
        registerGui(key) { runtime ->
            val window: Window = WindowImpl(key, 54, null, viewportl, content)

            window
        }
    }

    override fun createViewAndThen(guiId: String, callback: Consumer<ViewRuntime>, vararg viewers: UUID) {
        getGui(guiId).ifPresent { constructor ->
            val viewerSet = mutableSetOf(*viewers)
            val viewManagersForID = cachedViewRuntimes[guiId]
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

    private fun registerGui(id: String, constructor: Function<ViewRuntime, Window>) {
        entriesMap[id] = constructor
    }

    override fun createViewAndOpen(guiID: String, vararg viewers: UUID) {
        createViewAndThen(guiID, { it.open() }, *viewers)
    }

    override fun getGui(id: String): Optional<Function<ViewRuntime, Window>> {
        return Optional.ofNullable(
            entriesMap[id]
        )
    }

}
