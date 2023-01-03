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

public interface GuiViewManager<D extends Data> {

    void openNew(String... path);

    void openNew();

    void openPrevious();

    D getData();

    Cluster<D> getRoot();

    Optional<MenuComponent<D>> getCurrentMenu();

    Set<UUID> getViewers();

    WolfyUtils getWolfyUtils();

}
