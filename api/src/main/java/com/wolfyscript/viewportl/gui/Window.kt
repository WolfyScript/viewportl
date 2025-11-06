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
import com.wolfyscript.viewportl.gui.callback.TextInputCallback
import com.wolfyscript.viewportl.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.rendering.Renderer
import net.kyori.adventure.text.Component

/**
 * Holds the composition of the GUI and other properties of the window frame (such as title, background, etc.)
 */
interface Window {

    fun render(renderer: Renderer<*>)

    fun close()

    /**
     * The type of inventory used for this window.
     *
     * @return The specified type; or default [WindowType.CUSTOM]
     * @see size
     */
    val type: WindowType

    /**
     * Gets the size that is configured for the specified [window type][type].
     * Ignored when the [type] does not support a custom size.
     *
     * @return The specified size: or empty Optional when no size is configured.
     */
    var size: Int

    /**
     * Returns the current title of this window.
     */
    var title: Component?

    var resourcePath: String?

    /**
     * Gets the unique id (in context of the parent) of this Window.
     *
     * @return The id of this component.
     */
    val id: Key?

    /**
     * Gets the Viewportl instance this Window belongs to
     *
     * @return The Viewportl instance
     */
    val viewportl: Viewportl

    /**
     * Gets the width of this Window in slot count.
     *
     * @return The width in slots.
     */
    fun width(): Int

    /**
     * Gets the width of this Window in slot count.
     *
     * @return The height in slots.
     */
    fun height(): Int

    var onTextInput: TextInputCallback?

    var onTextInputTabComplete: TextInputTabCompleteCallback?

}
