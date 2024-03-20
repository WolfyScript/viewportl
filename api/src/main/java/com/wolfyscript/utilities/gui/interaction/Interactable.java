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

package com.wolfyscript.utilities.gui.interaction;

import com.wolfyscript.utilities.gui.GuiHolder;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;

public interface Interactable {

    /**
     * Called when an interaction occurs inside the Component.<br>
     * This may be called if a child Component is interacted with, for example a Button will cause this interaction to<br>
     * propagate from the root Cluster, down the Windows to the Button that caused the interaction to be called.<br>
     * <br>
     * For this behaviour any implementation must first call the parent interaction, before continuing.<br>
     * Only if there is no parent available (root Component) it continues, going back to the interaction cause.<br>
     *
     * @param holder             The holder that caused the interaction.
     * @param interactionDetails The details about the interaction.
     * @return The interaction result.
     */
    InteractionResult interact(GuiHolder holder, InteractionDetails interactionDetails);

    /**
     * Called whenever an interaction occurs.<br>
     * This propagates from the root Component to the Component that caused the interaction.
     *
     * @return The interaction callback
     */
    InteractionCallback interactCallback();
}
