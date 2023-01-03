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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class GuiViewManagerCommonImpl<D extends Data> implements GuiViewManager<D> {

    private final WolfyUtils wolfyUtils;
    private final Cluster<D> root;
    private final Deque<MenuComponent<D>> history;
    private final Set<UUID> viewers;
    private final D data;

    protected GuiViewManagerCommonImpl(WolfyUtils wolfyUtils, Cluster<D> rootCluster, Set<UUID> viewers) {
        this.wolfyUtils = wolfyUtils;
        this.root = rootCluster;
        this.history = new ArrayDeque<>();
        this.viewers = viewers;
        // Construct custom data instance
        Injector injector = Guice.createInjector(binder -> {
            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
            binder.bind(Cluster.class).toInstance(root);
            binder.bind(new TypeLiteral<GuiViewManager<D>>(){}).toInstance(this);
            binder.bind(new TypeLiteral<Set<UUID>>(){}).toInstance(viewers);
        });
        data = injector.getInstance(rootCluster.dataType());
    }

    public D getData() {
        return data;
    }

    @Override
    public void openNew(String... path) {
        root.getChild(path).ifPresent(component -> {
            if (component instanceof MenuComponent<D> menu) {
                for (UUID viewer : viewers) {
                    menu.open(this, viewer);
                }
                history.push(menu); // push the new menu to the history
            } else {
                throw new IllegalArgumentException("Cannot open non-menu Component!");
            }
        });
    }

    @Override
    public void openPrevious() {
        history.poll(); // Remove active current menu
        getCurrentMenu().ifPresent(previous -> {
            // Do not add menu to history, as it is already available
            viewers.forEach(uuid -> previous.open(this, uuid));
        });
    }

    @Override
    public Optional<MenuComponent<D>> getCurrentMenu() {
        return Optional.ofNullable(history.peek());
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Cluster<D> getRoot() {
        return root;
    }

    @Override
    public Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }
}
