/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.scafall.function.ReceiverConsumer
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Stream
import kotlin.collections.set

class GuiAPIManagerImpl(private val wolfyUtils: WolfyUtils) :
    GuiAPIManager {
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

    override fun registerGui(key: String, windowConsumer: ReceiverConsumer<Window>) {
        // TODO: maybe wrap in an extra object?
        registerGui(key) { runtime ->
            val buildContext = BuildContext(
                runtime,
                (runtime as ViewRuntimeImpl).reactiveSource,
                wolfyUtils
            )
            val window: Window = WindowImpl(key, 54, null, wolfyUtils, buildContext)
            with(windowConsumer) { window.consume() }
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
                ScafallProvider.get().scheduler.asyncTask(ScafallProvider.get().corePlugin) {
                    val viewManager = ViewRuntimeImpl(wolfyUtils, constructor, viewerSet)
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
