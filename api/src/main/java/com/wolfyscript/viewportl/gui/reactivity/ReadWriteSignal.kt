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
package com.wolfyscript.viewportl.gui.reactivity

import java.util.function.Function
import kotlin.reflect.KProperty

/**
 * Keeps track of value changes and updates Effects listening to a Signal accordingly.
 *
 * @param <V> The value type this Signal holds. Can be any Object.
 */
interface ReadWriteSignal<V : Any?> : SignalTaggable, ReadOnlySignal<V> {
    /**
     * Sets the tracked value to a new value and causes an update.
     *
     * @param newValue The new value to apply.
     */
    fun set(newValue: V)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V)

    /**
     * Gets the tracked value, then updates it, and causes an update.
     *
     * @param updateFunction The function to update the value.
     */
    fun update(updateFunction: Function<V, V>)

}
