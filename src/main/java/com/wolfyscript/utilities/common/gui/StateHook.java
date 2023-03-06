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

package com.wolfyscript.utilities.common.gui;

import java.util.function.Function;

/**
 * StateHooks are used to keep track of the component data and update the component when that data changes.
 *
 * @param <V> The type of the value this hook holds.
 */
public interface StateHook<V> {

    /**
     * Gets the currently active data for this hook.
     *
     * @return The current data of this hook.
     */
    V get();

    /**
     * Sets a new value for this hook.<br>
     * Whenever that new value is unequal to the current value it causes the component to update.<br>
     * In case a non-primitive value is used, then {@link Object#equals(Object)} should be implemented.
     *
     * @param newValue The new value for this hook.
     * @see #set(Function)
     */
    void set(V newValue);

    /**
     * Updates the current value of this hook.<br>
     * This method will always cause the component to update.<br>
     * It should be preferred over {@link #set(V)} whenever it is a more complex object,
     * where mutation is faster, then reallocating it.
     *
     * @param update The function used to update the value.
     * @see #set(V)
     */
    void set(Function<V, V> update);

}
