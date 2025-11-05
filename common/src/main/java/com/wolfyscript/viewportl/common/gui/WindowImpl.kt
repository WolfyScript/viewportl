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
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component

class WindowImpl internal constructor(
    override val id: Key,
    override var size: Int = 54,
    override val type: WindowType = WindowType.CUSTOM,
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
        return size / height()
    }

    override fun height(): Int {
        return size / 9
    }

}
