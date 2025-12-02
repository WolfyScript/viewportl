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

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.compose.LayoutNode
import com.wolfyscript.viewportl.common.gui.compose.RootMeasurePolicy
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.input.TextInputCallback
import com.wolfyscript.viewportl.gui.input.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.model.LocalStoreOwner
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import net.kyori.adventure.text.Component
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.CoroutineContext

class ViewImpl internal constructor(
    parentCoroutineContext: CoroutineContext,
    override val id: Key,
    override var size: Int = 54,
    override val type: WindowType = WindowType.CUSTOM,
    override val viewportl: Viewportl,
    val content: @Composable () -> Unit,
) : View, CoroutineScope {
    val runtimeClock: MonotonicFrameClock = parentCoroutineContext[MonotonicFrameClock]
        ?: throw IllegalStateException("Requires an external MonotonicFrameClock in the coroutine context!")

    // Coroutine setup
    val job = Job(parentCoroutineContext[Job])
    val compositionClock = BroadcastFrameClock {}
    override val coroutineContext = parentCoroutineContext + compositionClock + job

    // Composition
    val root = LayoutNode().apply {
        measurePolicy = RootMeasurePolicy
    }
    val recomposer = Recomposer(coroutineContext)
    val composition = Composition(
        applier = ModelNodeApplier(root) {
            requireLayout = true
        },
        parent = recomposer
    )

    // Window state
    override var title: Component? = null
    override var resourcePath: String? = null

    override var onTextInput: TextInputCallback? = null
    override var onTextInputTabComplete: TextInputTabCompleteCallback? = null

    private var activeRenderer: Renderer<*>? = null

    private val applyObserverHandle: ObserverHandle
    private val readStatesOnLayout = mutableSetOf<Any>()
    private val readStatesOnLayoutObserver: (Any) -> Unit = readStatesOnLayout::add
    private val readStatesOnDraw = mutableSetOf<Any>()
    private val readStatesOnDrawObserver: (Any) -> Unit = readStatesOnDraw::add

    private var requireLayout = true
    private var requireDraw = true

    init {
//        composition.setContent(content)
        GlobalSnapshotManager().ensureStarted(this)
        applyObserverHandle = registerSnapshotApplyObserver()
        startRecomposerImmediate()
        startFrameListenerImmediate()
    }

    /**
     * Measures the root node and places it. This is recursively measures and then places all the child nodes.
     */
    private fun measureAndPlace() {
        val rootConstraints = Constraints(Dp.Zero, width().slots, Dp.Zero, height().slots)
        root.arranger.remeasure(rootConstraints)
        root.arranger.layout()
    }

    private fun registerSnapshotApplyObserver(): ObserverHandle {
        return Snapshot.registerApplyObserver { changed, _ ->
            if(!requireLayout) {
                var hasRequiredDraw = requireDraw
                for (state in changed) {
                    if (state in readStatesOnLayout) {
                        requireLayout = true
                        break
                    }
                    if (hasRequiredDraw) {
                        break
                    }
                    if (state in readStatesOnDraw) {
                        requireDraw = true
                        hasRequiredDraw = true
                    }
                }
            }
        }
    }

    private fun startRecomposerImmediate() {
        launch {
            recomposer.runRecomposeAndApplyChanges()
            println("END RECOMPOSER")
        }
    }

    private fun startFrameListenerImmediate() {
        launch {
            do {
                runtimeClock.withFrameNanos { nanos ->
                    compositionClock.sendFrame(nanos)

                    if (requireLayout) {
                        layout()
                        render()
                    } else if (requireDraw) {
                        render()
                    }
                }
            } while (job.isActive)
            println("END FRAME LISTENER")
        }
    }

    suspend fun awaitCompletion() {
        try {
            val effectJob = checkNotNull(recomposer.effectCoroutineContext[Job]) {
                "No Job in effectCoroutineContext of recomposer"
            }
            effectJob.children.forEach { it.join() }
            recomposer.awaitIdle()

            launch { compositionClock.withFrameNanos { } }.join()

            recomposer.close()
            recomposer.join()
        } finally {
            job.cancel()
        }
        println("END COMPLETION")
    }

    /**
     * Performs the layout of the composition and observes any accessed states.
     */
    private fun layout() {
        requireLayout = false

        readStatesOnLayout.clear()
        Snapshot.observe(readObserver = readStatesOnLayoutObserver) {
            measureAndPlace()
        }
    }

    /**
     * Renders the composition and observes any accessed states that may cause it to be invalidated.
     */
    private fun render() {
        requireDraw = false

        readStatesOnDraw.clear()
        activeRenderer?.let { renderer ->
            Snapshot.observe(readObserver = readStatesOnDrawObserver) {
                renderer.render(root)
            }
        }
    }

    override fun render(runtime: UIRuntime, renderer: Renderer<*>) {
        activeRenderer = renderer

        composition.setContent {
            CompositionLocalProvider(
                LocalStoreOwner provides runtime.storeOwner,
                content = content
            )
        }
        requireLayout = true
        requireDraw = true
    }

    override fun close() {
        // TODO: Create a separate dispose function
        composition.dispose()
        recomposer.close()
    }

    override fun width(): Int {
        return size / height()
    }

    override fun height(): Int {
        return size / 9
    }

}

@OptIn(ExperimentalAtomicApi::class)
internal class GlobalSnapshotManager {
    private val started = AtomicBoolean(false)
    private val sent = AtomicBoolean(false)

    fun ensureStarted(scope: CoroutineScope) {
        if (started.compareAndSet(expectedValue = false, newValue = true)) {
            val channel = Channel<Unit>(1)
            scope.launch {
                channel.consumeEach {
                    sent.store(false)
                    Snapshot.sendApplyNotifications()
                }
            }
            Snapshot.registerGlobalWriteObserver {
                if (sent.compareAndSet(expectedValue = false, newValue = true)) {
                    channel.trySend(Unit)
                }
            }
        }
    }
}
