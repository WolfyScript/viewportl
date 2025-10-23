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
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import com.google.common.base.Preconditions
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.compose.LayoutNode
import com.wolfyscript.viewportl.common.gui.compose.RootMeasurePolicy
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.callback.TextInputCallback
import com.wolfyscript.viewportl.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.slotsSize
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component

class WindowImpl internal constructor(
    override val id: String,
    override var size: Int?,
    override val type: WindowType? = null,
    override val viewportl: Viewportl,
    val content: @Composable () -> Unit
) : Window {

    val root = LayoutNode().apply {
        measurePolicy = RootMeasurePolicy
    }
    val composition = Composition(
        applier = ModelNodeApplier(root),
        parent = Recomposer(Dispatchers.Main)
    )

    override var title: Component? = null
    override var resourcePath: String? = null

    override var onTextInput: TextInputCallback? = null
    override var onTextInputTabComplete: TextInputTabCompleteCallback? = null

    init {
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!")
    }

    override fun open() {
        composition.setContent {
            content()
        }
        measureAndPlace()

        // TODO: Render
    }

    private fun measureAndPlace() {
        val rootConstraints = Constraints(0.slotsSize, width().slotsSize, 0.slotsSize, height().slotsSize)
        root.arranger.remeasure(rootConstraints)
        root.arranger.layout()
    }

    private fun render() {

    }

    override fun close() {
        // TODO: Create a separate dispose function
        composition.dispose()
    }

    override fun width(): Int {
        return size?.div(height()) ?: 9
    }

    override fun height(): Int {
        return size?.div(9) ?: 1
    }

}

class WindowScopeImpl(val window: Window) : WindowScope {

    override var size: Int?
        get() = window.size
        set(value) { window.size = value }

    override var title: Component?
        get() = window.title
        set(value) { window.title = value }

    override fun onTextInput(inputCallback: TextInputCallback?) {
        window.onTextInput = inputCallback
    }

    override fun onTextInputTabComplete(textInputTabCompleteCallback: TextInputTabCompleteCallback?) {
        window.onTextInputTabComplete = textInputTabCompleteCallback
    }
}
