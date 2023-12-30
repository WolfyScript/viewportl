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

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;

import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.UUID;

@KeyedStaticId(key = "router")
public final class RouterImpl implements Router {

    private final String id;
    private final WolfyUtils wolfyUtils;
    private final Window window;
    private final Router parent;
    private final BiMap<String, Router> subRoutes = HashBiMap.create();
    private final InteractionCallback interactionCallback;

    RouterImpl(String id, WolfyUtils wolfyUtils, WindowBuilder windowBuilder, Router parent, InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.id = id;
        this.parent = parent;
        this.wolfyUtils = wolfyUtils;
        this.interactionCallback = interactionCallback;
        this.window = windowBuilder.create(this);
    }

    @Override
    public RenderContext createContext(GuiViewManager guiViewManager, Deque<String> path, UUID uuid) {
        return null;
    }

    @Override
    public Window open(GuiViewManager viewManager, String... path) {
        if (path == null || path.length == 0) {
            Window window1 = getWindow().orElseThrow(() -> new IllegalArgumentException(String.format("Path not found for router '%s'", id)));
            window1.open(viewManager);
            return window1;
        } else {
            Router subRoute = subRoutes.get(path[0]);
            if (subRoute != null) {
                String[] subPath = Arrays.copyOfRange(path, 1, path.length);
                return subRoute.open(viewManager, subPath);
            }
        }
        return null;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Router parent() {
        return parent;
    }

    @Override
    public Optional<Router> getSubRoute(String routeID) {
        return Optional.ofNullable(subRoutes.get(routeID));
    }

    @Override
    public Optional<Window> getWindow() {
        return Optional.ofNullable(window);
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, InteractionDetails interactionDetails) {
        return InteractionResult.cancel(false);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

}
