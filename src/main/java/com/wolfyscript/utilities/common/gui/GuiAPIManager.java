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

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Handles the general GUI API and acts as an entry point to the whole creation of {@link Cluster}s and {@link GuiViewManager}s.<br>
 * It stores all the registered {@link Cluster}s and allows to register new clusters via builders.<br>
 * Additionally, it stores the {@link GuiViewManager}s that handle the views for players.
 */
public interface GuiAPIManager {

    /**
     * Registers a new cluster under the given id, using the specified custom {@link Data} type ({@link D}).<br>
     * The builder consumer provides the newly constructed {@link ClusterComponentBuilder<D>}, which can then be used inside that consumer.<br>
     * <b>It is not required to call the {@link ClusterComponentBuilder#create()} method, because that is done after the consumer function ends.</b>
     *
     * @param id The unique id of the cluster to register.
     * @param dataType The type of the custom {@link Data} implementation.
     * @param clusterBuilderConsumer The consumer that provides the new builder.
     * @param <D> The type of the specified data implementation.
     */
    <D extends Data> void registerCluster(String id, Class<D> dataType, Consumer<ClusterComponentBuilder<D>> clusterBuilderConsumer);

    /**
     * Gets the registered cluster with the specified id.<br>
     * If the cluster is available it checks if it matches the provided type.<br>
     * When the type does not match an empty Optional is returned instead.
     *
     * @param id The id of the cluster.
     * @param dataType The type of the cluster.
     * @return The registered cluster only if the id and data type matches; otherwise empty Optional.
     * @param <D> The type of the data implementation.
     */
    <D extends Data> Optional<Cluster<D>> getCluster(String id, Class<D> dataType);

    /**
     * Gets the registered cluster with the specified id.<br>
     *
     * @param id The id of the cluster.
     * @return The registered cluster only if the id matches; otherwise empty Optional.
     */
    Optional<Cluster<?>> getCluster(String id);

    /**
     * Creates a new view for the specified viewers, with the specified cluster as its root.<br>
     * This gets the registered cluster using {@link #getCluster(String, Class)}.
     *
     * @param clusterId The id of the root cluster.
     * @param dataType The type of the cluster data implementation.
     * @param viewers The viewers of this view.
     * @return The newly created view.
     * @param <D> The type of the data implementation.
     */
    <D extends Data> GuiViewManager<D> createView(String clusterId, Class<D> dataType, UUID... viewers);

    /**
     * Same as {@link #createView(String, Class, UUID...)} and opens the entry menu right after the creation of the view.
     *
     * @param clusterID The id of the root cluster.
     * @param dataType The type of the cluster data implementation.
     * @param viewers The viewers of this view.
     * @return The newly created view.
     * @param <D> The type of the data implementation.
     */
    <D extends Data> GuiViewManager<D> createViewAndOpen(String clusterID, Class<D> dataType, UUID... viewers);

}
