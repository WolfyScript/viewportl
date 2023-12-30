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

package com.wolfyscript.utilities.gui.components;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.Position;
import com.wolfyscript.utilities.gui.Positionable;
import com.wolfyscript.utilities.gui.SignalledObject;

import java.util.Objects;

/**
 * <p>
 * Contains the common properties of all Components.
 * It makes it easier to create custom components.
 * </p>
 * <p>
 * Additional functionality should be implemented on a per-component basis without further inheritance, to make it easier to expand/change in the future.
 * Instead, use interfaces (that are already there for the platform independent API) and implement them for each component.
 * Duplicate code may occur, but it can be put into static methods.
 * </p>
 */
public abstract class AbstractComponentImpl implements Component, SignalledObject, Positionable {

    private final NamespacedKey type;
    private final String internalID;
    private final WolfyUtils wolfyUtils;
    private final Component parent;
    private final Position position;

    public AbstractComponentImpl(String internalID, WolfyUtils wolfyUtils, Component parent, Position position) {
        Preconditions.checkNotNull(internalID);
        Preconditions.checkNotNull(wolfyUtils);
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        Preconditions.checkNotNull(type, "Missing type key! One must be provided to the Component using the annotation: %s", KeyedStaticId.class.getName());
        this.internalID = internalID;
        this.wolfyUtils = wolfyUtils;
        this.parent = parent;
        this.position = position;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    @Override
    public String getID() {
        return internalID;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Component parent() {
        return parent;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractComponentImpl that = (AbstractComponentImpl) o;
        return Objects.equals(type, that.type) && Objects.equals(internalID, that.internalID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, internalID);
    }
}
