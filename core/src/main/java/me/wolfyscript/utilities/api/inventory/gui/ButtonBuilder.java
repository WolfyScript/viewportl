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

package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;

/**
 * Interface that contains method to create new button builders.
 * It is implemented by either {@link GuiWindow} or {@link GuiCluster} and will create the builders accordingly.
 *
 * @param <C> The type of the custom cache.
 */
public interface ButtonBuilder<C extends CustomCache> {

    /**
     * Gets a new builder for a {@link ChatInputButton<C>}.
     *
     * @param id The id of the new button.
     * @return The new builder.
     */
    ChatInputButton.Builder<C> chatInput(String id);

    /**
     * Gets a new builder for a {@link ActionButton<C>}.
     *
     * @param id The id of the new button.
     * @return The new builder.
     */
    ActionButton.Builder<C> action(String id);

    /**
     * Gets a new builder for a {@link DummyButton<C>}.
     *
     * @param id The id of the new button.
     * @return The new builder.
     */
    DummyButton.Builder<C> dummy(String id);

    /**
     * Gets a new builder for a {@link ItemInputButton<C>}.
     *
     * @param id The id of the new button.
     * @return The new builder.
     */
    ItemInputButton.Builder<C> itemInput(String id);

    /**
     * Gets a new builder for a {@link ToggleButton<C>}.
     *
     * @param id The id of the new button.
     * @return The new builder.
     */
    ToggleButton.Builder<C> toggle(String id);

    /**
     * Gets a new builder for a {@link MultipleChoiceButton<C>}.
     *
     * @param id The id of the new button.
     * @return The new builder.
     */
    MultipleChoiceButton.Builder<C> multiChoice(String id);

}
