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
package com.wolfyscript.viewportl.gui

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.compose.ViewProperties
import com.wolfyscript.viewportl.gui.compose.ViewPropertiesOverride
import com.wolfyscript.viewportl.gui.input.TextInputCallback
import com.wolfyscript.viewportl.gui.input.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.rendering.Renderer

/**
 * Holds the composition of the UI and other properties of the window frame (such as title, background, etc.)
 */
interface View {

    /**
     * Renders this window state using the specified [renderer]
     */
    fun render(renderer: Renderer<*>)

    fun close()

    var resourcePath: String?

    /**
     * Gets the unique id (in context of the parent) of this Window.
     *
     * @return The id of this component.
     */
    val id: Key?

    val properties: ViewProperties

    fun overrideProperties(key: Key, properties: ViewPropertiesOverride)

    fun removePropertiesOverride(key: Key)

    /**
     * Gets the Viewportl instance this Window belongs to
     *
     * @return The Viewportl instance
     */
    val viewportl: Viewportl

    var onTextInput: TextInputCallback?

    var onTextInputTabComplete: TextInputTabCompleteCallback?

}
