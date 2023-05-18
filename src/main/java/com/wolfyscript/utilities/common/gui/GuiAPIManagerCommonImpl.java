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

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.components.Router;
import java.util.Optional;
import java.util.UUID;

public abstract class GuiAPIManagerCommonImpl implements GuiAPIManager {

    protected final WolfyUtils wolfyUtils;
    private final BiMap<String, Router> clustersMap = HashBiMap.create();

    public GuiAPIManagerCommonImpl(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
    }

    protected void registerCluster(Router router) {
        Preconditions.checkArgument(!clustersMap.containsKey(router.getID()), "A cluster with the id '" + router.getID() + "' is already registered!");
        clustersMap.put(router.getID(), router);
    }

    public GuiViewManager createViewAndOpen(String clusterID, UUID... players) {
        GuiViewManager handler = createView(clusterID, players);
        handler.openNew();
        return handler;
    }

    @Override
    public Optional<Router> getRouter(String id) {
        return Optional.ofNullable(clustersMap.get(id));
    }
}
