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

import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.Deque;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 *     The Cluster acts as a structure and root Component for Menus.
 *     It can have both other Clusters and Windows as children.
 * </p>
 * The entry is used to define the default menu that is opened.
 *
 * @param <D> The type of the data implementation.
 */
public interface Router extends Interactable {

    String getID();

    WolfyUtils getWolfyUtils();

    Router parent();

    Optional<Router> getSubRoute(String routeID);

    Optional<Window> getWindow();

    Window open(GuiViewManager viewManager, String... path);

    /**
     * Opens this component for the specified view and player.<br>
     *
     * @param viewManager The view manager to open.
     * @param uuid The uuid to open the Window for.
     */
    RenderContext createContext(GuiViewManager viewManager, Deque<String> path, UUID uuid);
}
