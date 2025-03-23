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

import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.function.ReceiverFunction
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.callback.TextInputCallback
import com.wolfyscript.viewportl.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import net.kyori.adventure.text.Component

interface Window {

    fun open()

    fun close()

    /**
     * Gets the type that is configured for this Window.<br></br>
     * **When this is empty, then [.getSize] will return the specified size.**
     *
     * @return The specified type; or empty Optional when no type is configured.
     * @see .getSize
     */
    val type: WindowType?

    /**
     * Gets the size that is configured for this Window.<br></br>
     *
     * **When this is empty, then [.getType] will return the specified type.**
     *
     * @return The specified size: or empty Optional when no size is configured.
     */
    var size: Int?

    /**
     * Returns the current title of this window.
     */
    var title: Component?

    fun title(titleUpdate: ReceiverFunction<Component?, Component?>)

    var resourcePath: String?

    /**
     * Gets the unique id (in context of the parent) of this Window.
     *
     * @return The id of this component.
     */
    val id: String?

    /**
     * Gets the Viewportl instance this Window belongs to
     *
     * @return The Viewportl instance
     */
    val viewportl: Viewportl

    val scaffolding: Scafall
        get() = ScafallProvider.get()

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

interface WindowScope : ComponentScope {

    /**
     * Setups an effect that updates the title everytime the signal sources are updated.
     */
    fun title(titleUpdate: ReceiverFunction<Component?, Component?>)

    /**
     * The initial size of the window inventory
     */
    var size: Int?

    /**
     * The title of the window.
     * Note: This will get replaced if a title update effect is applied
     */
    var title: Component?

    fun onTextInput(inputCallback: TextInputCallback?)

    fun onTextInputTabComplete(textInputTabCompleteCallback: TextInputTabCompleteCallback?)

}
