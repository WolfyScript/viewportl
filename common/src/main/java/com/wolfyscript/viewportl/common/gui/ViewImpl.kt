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
import com.wolfyscript.viewportl.gui.ViewType
import com.wolfyscript.viewportl.gui.input.TextInputCallback
import com.wolfyscript.viewportl.gui.input.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.compose.ViewProperties
import com.wolfyscript.viewportl.gui.compose.ViewPropertiesOverride
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.model.LocalStoreOwner
import com.wolfyscript.viewportl.gui.model.LocalView
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.util.UUID
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.CoroutineContext

val DefaultViewProperties = ViewProperties(
    type = ViewProperties.Type(ViewType.CUSTOM),
    dimensions = ViewProperties.Dimensions(9.slots, 6.slots),
    title = ViewProperties.Title(null),
)

class ViewImpl internal constructor(
    parentCoroutineContext: CoroutineContext,
    override val viewer: UUID,
    override val viewportl: Viewportl,
    val runtime: UIRuntime,
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

    // State
    override var resourcePath: String? = null

    private val keyedPropertyStack: MutableMap<Key, ViewPropertiesOverride> = mutableMapOf()
    override var properties: ViewProperties = DefaultViewProperties
        private set

    override fun overrideProperties(key: Key, properties: ViewPropertiesOverride) {
        if (keyedPropertyStack.containsKey(key)) {
            error("An override with the key $key already exists!")
        }
        keyedPropertyStack[key] = properties
        updateProperties()
    }

    override fun removePropertiesOverride(key: Key) {
        keyedPropertyStack.remove(key)
        updateProperties()
    }

    private fun updateProperties() {
        var override: ViewPropertiesOverride? = null
        for (value in keyedPropertyStack.values) {
            override = value.override(override)
        }
        this.properties = ViewProperties(
            type = override?.type ?: DefaultViewProperties.type,
            dimensions = override?.dimensions ?: DefaultViewProperties.dimensions,
            title = override?.title ?: DefaultViewProperties.title,
        )
        // TODO: require relayout and rerender
        requireLayout = true
        requireDraw = true
        activeRenderer?.onViewChange(runtime, this)
    }

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
        val rootConstraints = Constraints(Dp.Zero, properties.dimensions.width, Dp.Zero, properties.dimensions.height)
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
                renderer.render(this, root)
            }
        }
    }

    override fun render(renderer: Renderer<*>) {
        activeRenderer = renderer

        composition.setContent {
            CompositionLocalProvider(
                LocalStoreOwner provides runtime,
                LocalView provides this,
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
