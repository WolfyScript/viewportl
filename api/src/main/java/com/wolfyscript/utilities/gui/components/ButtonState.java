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

package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.callback.RenderCallback;
import com.wolfyscript.utilities.gui.StateSelector;

/**
 * <p>
 * The State represents a State of a Button, that handles the functionality of the Button.<br>
 * It provides several runtime callbacks that are called whenever an interaction or render request occurs.
 * </p>
 * <p>
 * These States are used to draw the proper icons and textures of the GUI and handle the interactions and data manipulation.
 * <br>
 * Buttons like ToggleButtons use two states, and can sync it to the provided state using the {@link StateSelector}.
 * </p>
 */
public interface ButtonState {

    /**
     * The key of the state. This is mostly used for translations.<br>
     * By default, it is the same as the owner Component.
     * But it can be different or a sub key from the owner key.
     *
     * @return The key of the state.
     */
    String key();

    /**
     * Called whenever an interaction occurs.<br>
     * This propagates from the root Component to the Component that caused the interaction.
     *
     * @return The interaction callback
     */
    InteractionCallback interactCallback();

    /**
     * Called each time the Component or a child Component is rendered in the GUI.
     *
     *
     * @return
     */
    RenderCallback renderCallback();

    interface Builder<S extends ButtonState> {

        /**
         * Specifies the sub key of the state relative to the owner key or previously specified key or subKey, separated by a dot ('.').
         * These keys are used as language keys to lookup translations in the lang files.
         *
         * @param subKey The sub key of this state.
         * @return This Builder for chaining.
         */
        Builder<S> subKey(String subKey);

        /**
         * Specifies the key of the state.
         * These keys are used as language keys to lookup translations in the lang files.
         *
         * @param key The key of this state.
         * @return This Builder for chaining.
         */
        Builder<S> key(String key);

        Builder<S> interact(InteractionCallback interactionCallback);

        Builder<S> render(RenderCallback renderCallback);

        S create();

    }

}
