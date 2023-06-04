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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The GuiViewManager, as the name suggests, manages a view of the GUI for one or more players.<br>
 * It contains the custom Data object that stores all the required data of this view.<br>
 *
 * The view is immutable, so you need to create a new view each time you need to add a viewer or change the root.
 *
 */
public interface GuiViewManager {

    /**
     * Opens a new menu under the specific path.
     * When the component at the specified path cannot be rendered (is not a window) it'll use the entry of that component.
     *
     * @param path The path to the menu to open.
     */
    void openNew(String... path);

    /**
     * Opens the entry menu of this root cluster.
     */
    void openNew();

    /**
     * Opens the currently active menu without updating the history.<br>
     * In case there is no active menu it opens the entry of the root cluster.
     */
    void open();

    /**
     * Goes back to the previously opened menu and opens it.
     */
    void openPrevious();

    /**
     * The router of this view manager.
     *
     * @return The root cluster of this view manager.
     */
    Router getRouter();

    Optional<WindowState> getCurrentWindowState();

    /**
     * Gets the currently active menu.
     *
     * @return The currently active menu.
     */
    Optional<Window> getCurrentMenu();

    /**
     * Gets the viewers that are handled by this view manager.
     * When using these UUIDS, make sure the associated player is actually online!
     *
     * @return A Set of the viewers, that are handled by this manager.
     */
    Set<UUID> getViewers();

    /**
     * The API instance this manager belongs to.
     *
     * @return The API instance of this manager.
     */
    WolfyUtils getWolfyUtils();

    Optional<RenderContext> getRenderContext(UUID viewer);

}
