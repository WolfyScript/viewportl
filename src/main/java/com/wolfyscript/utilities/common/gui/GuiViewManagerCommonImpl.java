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
import com.wolfyscript.utilities.common.gui.components.Router;
import com.wolfyscript.utilities.common.gui.components.Window;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class GuiViewManagerCommonImpl implements GuiViewManager {

    private final WolfyUtils wolfyUtils;
    private final Router router;
    private Window currentRoot;
    private final Deque<Window> history;
    private final Set<UUID> viewers;

    protected GuiViewManagerCommonImpl(WolfyUtils wolfyUtils, Router router, Set<UUID> viewers) {
        this.wolfyUtils = wolfyUtils;
        this.router = router;

        this.history = new ArrayDeque<>();
        this.viewers = viewers;
        // Construct custom data instance
        Injector injector = Guice.createInjector(binder -> {
            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
            binder.bind(Router.class).toInstance(router);
            binder.bind(new TypeLiteral<GuiViewManager>() {
            }).toInstance(this);
            binder.bind(new TypeLiteral<Set<UUID>>() {
            }).toInstance(viewers);
        });
    }

    @Override
    public void openNew() {
        openNew(new String[0]);
    }

    @Override
    public void open() {
        if (history.isEmpty()) {
            openNew();
        } else {
            getCurrentMenu().ifPresent(window -> window.open(this));
        }
    }

    @Override
    public void openPrevious() {
        history.poll(); // Remove active current menu
        setCurrentRoot(history.peek());
        getCurrentMenu().ifPresent(previous -> {
            // Do not add menu to history, as it is already available
            previous.open(this);
        });
    }

    public void setCurrentRoot(Window currentRoot) {
        this.currentRoot = currentRoot;
    }

    @Override
    public Optional<Window> getCurrentMenu() {
        return Optional.ofNullable(currentRoot);
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Router getRoot() {
        return router;
    }

    @Override
    public Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }
}
