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

import java.util.UUID;
import java.util.function.Consumer;

public interface GuiAPIManager {

    <D extends Data> void registerCluster(String id, Class<D> dataType, Consumer<ClusterComponentBuilder<D>> clusterBuilderConsumer);

    <D extends Data> GuiViewManager<D> createView(String clusterId, Class<D> dataType, UUID... viewers);

    <D extends Data> GuiViewManager<D> createViewAndOpen(String clusterID, Class<D> dataType, UUID... players);

}
