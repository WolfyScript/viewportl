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

package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.gui.functions.ReceiverConsumer;
import com.wolfyscript.utilities.gui.reactivity.ReactiveSource;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Handles the general GUI API and acts as an entry point to the whole creation of {@link Router}s and {@link ViewRuntime}s.<br>
 * It stores all the registered {@link Router}s and allows to register new clusters via builders.<br>
 * Additionally, it stores the {@link ViewRuntime}s that handle the views for players.
 */
public interface GuiAPIManager {

    /**
     * Registers a new router under the given id.<br>
     * The builder consumer provides the newly constructed {@link RouterBuilder}, which can then be used inside that consumer.<br>
     *
     * @param id The unique id of the router to register.
     * @param routerBuilderConsumer The consumer that provides the new builder.
     */
    void registerGui(String guiID, ReceiverConsumer<RouterBuilder> routerBuilderConsumer);

    /**
     * Registers a new router that it loads from the specified gui data directory.
     * The consumer function provides that newly constructed {@link RouterBuilder}, which can be used to manipulate the builder.
     *
     * @param id The unique id of the router to register.
     * @param routerBuilderConsumer The function to manipulate the new builder.
     */
    void registerGuiFromFiles(String guiID, ReceiverConsumer<RouterBuilder> routerBuilderConsumer);

    /**
     * Gets the registered router with the specified id.<br>
     *
     * @param id The id of the router.
     * @return The registered router only if the id matches; otherwise empty Optional.
     */
    Optional<Function<ViewRuntime, RouterBuilder>> getGui(String id);

    /**
     * Creates a new view for the specified viewers, with the specified GUI.
     * <p>
     *     The view is build async, so care should be taken to not access any main-thread objects (e.g. Entities, World, etc.).<br>
     *     When such data is required inside the GUI use {@link ReactiveSource#resourceSync(BiFunction)}!
     * </p>
     * <p>
     *     The callback is run right after the creation (or retrieval) of the view manager.<br>
     *     <b>That means the callback may be ASYNC!</b>
     * </p>
     *
     * @param guiId The id of the gui.
     * @param callback The callback, that is run right after the view manager has been created. <b>May be Async!</b>
     * @param viewers The viewers of this view.
     */
    void createViewAndThen(String guiId, Consumer<ViewRuntime> callback, UUID... viewers);

    /**
     * Creates (or gets the existing ViewManager) and opens the entry menu right after the creation of the view.
     *
     * @param guiID The id of the gui.
     * @param viewers The viewers of this view.
     */
    void createViewAndOpen(String guiID, UUID... viewers);

    Stream<ViewRuntime> getViewManagersFor(UUID uuid);

    Stream<ViewRuntime> getViewManagersFor(UUID uuid, String guiID);

}
