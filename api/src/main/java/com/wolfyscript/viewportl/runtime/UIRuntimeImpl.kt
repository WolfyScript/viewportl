package com.wolfyscript.viewportl.runtime

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.DataStoreMap
import com.wolfyscript.viewportl.gui.model.SimpleDataStoreMap
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

internal class UIRuntimeImpl(
    override val viewportl: Viewportl,
    parentCoroutineContext: CoroutineContext,
    override val owner: UUID,
    override val renderer: Renderer<*>,
    override val interactionHandler: InteractionHandler<*>,
) : UIRuntime {

    override val id: Long = NEXT_ID++

    override val viewers: MutableSet<UUID> = mutableSetOf()
    var viewContent: (@Composable () -> Unit)? = null
    override val views: MutableMap<UUID, View> = mutableMapOf()

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
    override val sharedStore: DataStoreMap = SimpleDataStoreMap()
    private val viewerStores: MutableMap<UUID, SimpleDataStoreMap> = mutableMapOf()

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
                    runtimeClock.sendFrame(System.nanoTime())
                }

                delay(50.milliseconds)
            }
        }
    }

    override fun getViewerStore(viewer: UUID): DataStoreMap {
        return viewerStores[viewer] ?: throw IllegalStateException("Viewer $viewer not part of runtime")
    }

    override fun setContent(content: @Composable () -> Unit) {
        this.viewContent = content
        for (view in views.values) {
            view.close()
        }
        views.clear()
        for (viewer in viewers) {
            resetView(viewer, viewContent!!)
        }
        startMainLoop()
    }

    override fun dispose() {
        interactionHandler.dispose()
        views.forEach { it.value.close() }
        views.clear()
        viewContent = null
        running = false
    }

    override fun addViewer(uuid: UUID) {
        viewers.add(uuid)
        viewerStores[uuid] = SimpleDataStoreMap()
        if (viewContent != null && !viewers.contains(uuid)) {
            resetView(uuid, viewContent!!)
        }
    }

    private fun resetView(viewer: UUID, content: @Composable () -> Unit) {
        views[viewer] = ViewImpl(coroutineContext, viewer, viewportl, this, content)
    }

    override fun removeViewer(uuid: UUID) {
        viewers.remove(uuid)
        viewerStores.remove(uuid)
        views.remove(uuid)?.close()
    }

    override fun openViews() {
        for (view in views.values) {
            view.render(renderer)

            interactionHandler.onViewOpened(view)
            renderer.onViewInit(this@UIRuntimeImpl, view)
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