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

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.model.SimpleDataStoreMap
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.DataStoreMap
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.System.nanoTime
import java.util.*
import kotlin.coroutines.CoroutineContext

class UIRuntimeImpl(
    override val viewportl: Viewportl,
    parentCoroutineContext: CoroutineContext,
    override val owner: UUID,
    // Handlers that handle rendering and interaction
    override val renderer: Renderer<*>,
    override val interactionHandler: InteractionHandler<*>,
) : UIRuntime {
    override val id: Long = NEXT_ID++

    override val viewers: MutableSet<UUID> = mutableSetOf()
    override var view: View? = null

    /**
     * The runtime clock handles the timings of this runtime.
     * Open views use these timings to render and update.
     * That way if there are multiple viewers with different views they all use the correct timings and are synced.
     */
    val runtimeClock = BroadcastFrameClock { }
    override val coroutineContext: CoroutineContext = parentCoroutineContext + runtimeClock
    var running = true

    // TODO: GUI runtimes are completely async, so need a way to securely communicate with main thread from Composable
    // TODO: Rework data storage
    override val storeOwner: DataStoreMap = SimpleDataStoreMap()

    init {
        interactionHandler.init(this)
    }

    private fun startMainLoop() {
        running = true
        launch {
            while (running) {
                if (interactionHandler.isBusy) {
                    continue
                }

                if (renderer.requestsNewFrames) {
                    runtimeClock.sendFrame(nanoTime())
                } else {
                    // Renderer is rendering previous frame
                }

                delay(50)
            }
        }
    }

    override fun setNewView(id: Key, content: @Composable (() -> Unit)) {
        view = ViewImpl(coroutineContext, id, viewportl, this, content)
        startMainLoop()
    }

    override fun dispose() {
        interactionHandler.dispose()
        view?.close()
        view = null
        running = false
    }

    override fun joinViewer(uuid: UUID) {
        viewers.add(uuid)
        launch {
            view?.let {
                renderer.onViewInit(this@UIRuntimeImpl, it)
            }
        }
    }

    override fun leaveViewer(uuid: UUID) {
        viewers.remove(uuid)
    }

    override fun openView() {
        view?.let {
            it.render(renderer)

            interactionHandler.onViewOpened(it)
            renderer.onViewInit(this@UIRuntimeImpl, it)

            println("COMPLETE")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as UIRuntimeImpl
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    companion object {
        private var NEXT_ID = Long.MIN_VALUE
    }
}
