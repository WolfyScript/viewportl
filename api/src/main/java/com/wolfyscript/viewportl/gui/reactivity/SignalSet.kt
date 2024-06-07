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

package com.wolfyscript.viewportl.gui.reactivity

import java.util.function.Function

interface SignalSet<V> {

    /**
     * Sets the tracked value to a new value and causes a re-render.
     *
     * @param newValue The new value to apply.
     */
    fun set(newValue: V?)

    /**
     * Gets the tracked value, then updates it, and causes a re-render.
     *
     * @param updateFunction The function to update the value.
     */
    fun update(updateFunction: Function<V?, V?>?)
}