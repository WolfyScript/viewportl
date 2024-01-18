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

package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.GuiHolder;
import com.wolfyscript.utilities.gui.ViewRuntime;
import com.wolfyscript.utilities.gui.RenderContext;

public interface AnimationFrame {

    /**
     * Gets the duration of this frame in ticks
     *
     * @return The duration in ticks
     */
    int duration();

    /**
     * Gets the Animation this frame belongs to
     *
     * @return The Animation this frame belongs to
     */
    Animation<? extends AnimationFrame> animation();

    /**
     * Renders this frame into the GUI.<br>
     * The Context already entered the Component that this frames' animation belongs to.
     *
     * @param viewManager   The view manager to render this for
     * @param holder        The holder of the GUI
     * @param context       The rendering context
     */
    void render(ViewRuntime viewManager, GuiHolder holder, RenderContext context);

}
