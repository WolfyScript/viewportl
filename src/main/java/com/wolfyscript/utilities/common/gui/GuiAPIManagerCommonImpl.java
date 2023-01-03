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
import java.util.UUID;
import java.util.function.Consumer;

public abstract class GuiAPIManagerCommonImpl implements GuiAPIManager {

    protected final WolfyUtils wolfyUtils;
    private final BiMap<String, Cluster<?>> clustersMap = HashBiMap.create();

    public GuiAPIManagerCommonImpl(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
    }

    public abstract <D extends Data> void registerCluster(String id, Class<D> dataType, Consumer<ClusterComponentBuilder<D>> clusterBuilderConsumer);

    protected <D extends Data> void registerCluster(Cluster<D> cluster) {
        Preconditions.checkArgument(!clustersMap.containsKey(cluster.getID()), "A cluster with the id '" + cluster.getID() + "' is already registered!");
        clustersMap.put(cluster.getID(), cluster);
    }

    public <D extends Data> GuiViewManager<D> createViewAndOpen(String clusterID, Class<D> dataType, UUID... players) {
        GuiViewManager<D> handler = createView(clusterID, dataType, players);
        handler.openNew();
        return handler;
    }


}
