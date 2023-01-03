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

/**
 * <p>
 * The State represents a State of a Component, that handles the functionality of the Component.<br>
 * It provides several runtime callbacks that are called whenever an interaction or render request occurs.
 * </p>
 * <p>
 * For example inside Windows and Buttons these States are used to draw the proper icons and textures of the GUI and handle the interactions and data manipulation.
 * <br>
 * Buttons like ToggleButtons use two states, and can sync it to the provided data ({@link D}) using the {@link StateSelector}.
 * </p>
 * <p>
 * The State of a Component is independent of its children and won't change if a children changes its state.<br>
 * Instead, the state is selected based on the data of the {@link GuiViewManager<D>} each time an update occurs.
 * </p>
 *
 * @param <D> The type of data.
 */
public interface ComponentState<D extends Data> {

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
    InteractionCallback<D> interactCallback();

    /**
     * Called after each interaction.
     * This propagates from the root Component to the Component that caused the interaction.
     * This is called before the interaction of the child Component.
     *
     * @return The post interact callback
     */
    InteractionPostCallback<D> interactPostCallback();

    /**
     * Called before each render.
     *
     *
     * @return
     */
    RenderPreCallback<D> renderPreCallback();

    /**
     * Called each time the Component or a child Component is rendered in the GUI.
     *
     *
     * @return
     */
    RenderCallback<D> renderCallback();

    interface Builder<D extends Data, S extends ComponentState<D>> {

        /**
         * Specifies the sub key of the state relative to the owner key or previously specified key or subKey, separated by a dot ('.').
         * These keys are used as language keys to lookup translations in the lang files.
         *
         * @param subKey The sub key of this state.
         * @return This Builder for chaining.
         */
        Builder<D, S> subKey(String subKey);

        /**
         * Specifies the key of the state.
         * These keys are used as language keys to lookup translations in the lang files.
         *
         * @param key The key of this state.
         * @return This Builder for chaining.
         */
        Builder<D, S> key(String key);

        Builder<D, S> interact(InteractionCallback<D> interactionCallback);

        Builder<D, S> interactPost(InteractionPostCallback<D> interactPostCallback);

        Builder<D, S> renderPre(RenderPreCallback<D> renderPreCallback);

        Builder<D, S> render(RenderCallback<D> renderCallback);

        S create();

    }

}
