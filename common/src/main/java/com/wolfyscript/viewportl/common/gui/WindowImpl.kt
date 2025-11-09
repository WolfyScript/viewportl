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
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.compose.LayoutNode
import com.wolfyscript.viewportl.common.gui.compose.RootMeasurePolicy
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.callback.TextInputCallback
import com.wolfyscript.viewportl.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.slotsSize
import com.wolfyscript.viewportl.gui.rendering.Renderer
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import kotlin.coroutines.CoroutineContext

class WindowImpl internal constructor(
    parentCoroutineContext: CoroutineContext,
    override val id: Key,
    override var size: Int = 54,
    override val type: WindowType = WindowType.CUSTOM,
    override val viewportl: Viewportl,
    val content: @Composable () -> Unit,
) : Window, CoroutineScope {
    val runtimeClock: MonotonicFrameClock = parentCoroutineContext[MonotonicFrameClock]
        ?: throw IllegalStateException("Requires an external MonotonicFrameClock in the coroutine context!")

    // Coroutine setup
    val job = Job(parentCoroutineContext[Job])
    val clock = BroadcastFrameClock {}
    override val coroutineContext = parentCoroutineContext + clock + job

    // Composition
    val root = LayoutNode().apply {
        measurePolicy = RootMeasurePolicy
    }
    val recomposer = Recomposer(coroutineContext)
    val composition = Composition(
        applier = ModelNodeApplier(root),
        parent = recomposer
    )

    // Window state
    override var title: Component? = null
    override var resourcePath: String? = null

    override var onTextInput: TextInputCallback? = null
    override var onTextInputTabComplete: TextInputTabCompleteCallback? = null

    private var activeRenderer: Renderer<*>? = null

    init {
        startRecomposerImmediate()
        startFrameListenerImmediate()
    }

    /**
     * Measures the root node and places it. This is recursively measures and then places all the child nodes.
     */
    private fun measureAndPlace() {
        val rootConstraints = Constraints(0.slotsSize, width().slotsSize, 0.slotsSize, height().slotsSize)
        root.arranger.remeasure(rootConstraints)
        root.arranger.layout()
    }

    private fun startRecomposerImmediate() {
        launch(start = CoroutineStart.UNDISPATCHED) {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    private fun startFrameListenerImmediate() {
        launch(start = CoroutineStart.UNDISPATCHED) {
            do {
                runtimeClock.withFrameNanos { nanos ->
                    measureAndPlace()
                    activeRenderer?.render(root)
                }
            } while (job.isActive)
        }
    }

    override suspend fun render(renderer: Renderer<*>) {
        composition.setContent { content() }
        activeRenderer = renderer
    }

    override fun close() {
        // TODO: Create a separate dispose function
        composition.dispose()
    }

    override fun width(): Int {
        return size / height()
    }

    override fun height(): Int {
        return size / 9
    }

}
