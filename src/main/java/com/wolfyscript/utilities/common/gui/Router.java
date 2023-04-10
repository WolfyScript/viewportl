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
import java.util.Set;

/**
 * <p>
 *     The Cluster acts as a structure and root Component for Menus.
 *     It can have both other Clusters and Windows as children.
 * </p>
 * The entry is used to define the default menu that is opened.
 *
 * @param <D> The type of the data implementation.
 */
public interface Router extends MenuComponent<RouterState>, Stateful<RouterState>, Component, BranchComponent, Interactable {

    @Override
    Router parent();

    /**
     * Gets the entry menu Component that is opened by default.
     *
     * @return The entry Component.
     */
    RouterEntry entry();

    Optional<Router> getRoute(String routeID);

    Set<? extends Router> childRoutes();

    @Override
    Set<? extends Window> childComponents();

    @Override
    Optional<? extends Window> getChild(String... path);

}
