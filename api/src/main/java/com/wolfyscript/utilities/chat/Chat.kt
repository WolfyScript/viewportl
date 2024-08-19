/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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
package com.wolfyscript.utilities.chat

import com.wolfyscript.utilities.WolfyUtils
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Allows sending messages to players, with the specified prefix, translations, placeholders, etc.<br>
 * Additionally, this class provides a system to create text component click events, that execute specified callbacks.
 *
 */
interface Chat {

    val wolfyUtils: WolfyUtils

    /**
     * Gets the [MiniMessage] object, that allows you to parse text with formatting similar to html.<br></br>
     * See [MiniMessage docs](https://docs.adventure.kyori.net/minimessage/)
     *
     * @return The MiniMessage object
     */
    val miniMessage: MiniMessage

}
