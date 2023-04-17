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

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Handles the general GUI API and acts as an entry point to the whole creation of {@link Router}s and {@link GuiViewManager}s.<br>
 * It stores all the registered {@link Router}s and allows to register new clusters via builders.<br>
 * Additionally, it stores the {@link GuiViewManager}s that handle the views for players.
 */
public interface GuiAPIManager {

    /**
     * Registers a new router under the given id.<br>
     * The builder consumer provides the newly constructed {@link RouterBuilder}, which can then be used inside that consumer.<br>
     *
     * @param id The unique id of the router to register.
     * @param routerBuilderConsumer The consumer that provides the new builder.
     */
    void registerRouter(String id, Consumer<RouterBuilder> routerBuilderConsumer);

    /**
     * Registers a new router that it loads from the given file.
     * The consumer function provides that newly constructed {@link RouterBuilder}, which can be used to manipulate the builder.
     *
     * @param file The file to load the router from.
     * @param routerBuilderConsumer The function to manipulate the new builder.
     */
    void registerRouterFromFile(File file, Consumer<RouterBuilder> routerBuilderConsumer);

    /**
     * Gets the registered router with the specified id.<br>
     *
     * @param id The id of the router.
     * @return The registered router only if the id matches; otherwise empty Optional.
     */
    Optional<Router> getRouter(String id);

    /**
     * Creates a new view for the specified viewers, with the specified cluster as its root.<br>
     * This gets the registered cluster using {@link #getRouter(String)}.
     *
     * @param clusterId The id of the root cluster.
     * @param viewers The viewers of this view.
     * @return The newly created view.
     */
    GuiViewManager createView(String clusterId, UUID... viewers);

    /**
     * Same as {@link #createView(String, UUID...)} and opens the entry menu right after the creation of the view.
     *
     * @param clusterID The id of the root cluster.
     * @param viewers The viewers of this view.
     * @return The newly created view.
     */
    GuiViewManager createViewAndOpen(String clusterID, UUID... viewers);

}
