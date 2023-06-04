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

import java.util.Collection;
import java.util.function.Function;

/**
 * Keeps track of value changes and updates Components listening to a Signal accordingly.
 * Signals are shared across all children of the Component that creates the Signal,
 * meaning that children can listen to that of the parent, but not the other way around!
 *
 * @param <MT> The value type this Signal holds. Can be any Object.
 */
public interface Signal<MT> {

    /**
     * The key of this Signal.
     * Must be unique in the path of the component, meaning no parent nor child can create a Signal with the same key!
     *
     * @return The key of this Signal.
     */
    String key();

    /**
     * The type of the value that this signal tracks.
     *
     * @return The value type of this signal.
     */
    Class<MT> valueType();

    /**
     * Sets the tracked value to a new value and causes a re-render.
     *
     * @param newValue The new value to apply.
     */
    void set(MT newValue);

    /**
     * Gets the tracked value, then updates it, and causes a re-render.
     *
     * @param updateFunction The function to update the value.
     */
    void update(Function<MT, MT> updateFunction);

    /**
     * Gets the current value.
     *
     * @return The current value.
     */
    MT get();

    /**
     * Enters the context of the specified the {@link GuiViewManager}.
     * From this point onward the {@link #get()}, {@link #set(MT)}, {@link #set(MT)} return/manipulate the value of the specified view manager.
     *
     * @param viewManager The view manager to use.
     */
    void enter(GuiViewManager viewManager);

    /**
     * Exits the context of the current view manager or does nothing if no view manager is active.
     *
     * @return A boolean telling if the signal was updated since the last {@link #enter(GuiViewManager)}.
     */
    boolean exit();

    /**
     * The Builder is used to construct signals for Components (See ComponentBuilder).
     * The type is defined by whatever parent Builder constructs this Builder. Usually a ComponentBuilder.
     *
     * @param <T> The type of the value tracked.
     */
    interface Builder<T> {

        /**
         * The key of the Signal.
         * Must be unique in the path of the component, meaning no parent nor child can create a Signal with the same key!
         *
         * @return The key of the Signal.
         */
        String getKey();

        Class<T> getValueType();

        /**
         * Defines a function that is used by the signal to provide a default value, whenever requested by a for example ComponentState.
         *
         * @param defaultValueFunction The function to create a default value.
         * @return This Builder instance for chaining.
         */
        Builder<T> defaultValue(Function<ComponentState, T> defaultValueFunction);

        /**
         * Creates an instance of the Signal with the predefined settings of this builder.
         *
         * @return A new instance of a Signal.
         */
        Signal<T> create();

    }

}
