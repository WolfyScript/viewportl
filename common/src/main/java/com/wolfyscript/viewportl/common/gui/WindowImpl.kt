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

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Preconditions
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.elements.ComponentScopeImpl
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.callback.TextInputCallback
import com.wolfyscript.viewportl.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import net.kyori.adventure.text.Component

@StaticNamespacedKey(key = "window")
class WindowImpl internal constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("size") override var size: Int?,
    @JsonProperty("type") override val type: WindowType? = null,
    @JacksonInject("viewportl") override val viewportl: Viewportl,
) : Window {
    override var title: Component? = null
    override var resourcePath: String? = null

    override var onTextInput: TextInputCallback? = null
    override var onTextInputTabComplete: TextInputTabCompleteCallback? = null

    init {
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!")
    }

    override fun title(titleUpdate: Component?.() -> Component?) {}

    override fun open() {
        // TODO: Render graph
    }

    override fun close() {
//        context.runtime.modelGraph.removeNode(0)
    }

    override fun width(): Int {
        return size?.div(height()) ?: 9
    }

    override fun height(): Int {
        return size?.div(9) ?: 1
    }

}

class WindowScopeImpl(val window: Window, componentScope: ComponentScopeImpl) : WindowScope, ComponentScope by componentScope {

    override fun title(titleUpdate: Component?.() -> Component?) {
        createEffect {
            window.title = titleUpdate(window.title)
            runtime.into().renderer.updateTitle(window.title)
        }
    }

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
