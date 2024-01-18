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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;

import java.util.function.Consumer;

public interface Component extends Keyed {

    @JsonIgnore
    @Override
    NamespacedKey getNamespacedKey();

    @JsonGetter("type")
    default NamespacedKey type() {
        return getNamespacedKey();
    }

    /**
     * Gets the unique id (in context of the parent) of this component.
     *
     * @return The id of this component.
     */
    String getID();

    /**
     * Gets the global WolfyUtils instance, this component belongs to.
     *
     * @return The WolfyUtils API instance.
     */
    WolfyUtils getWolfyUtils();

    /**
     * The parent of this Component, or null if it is a root Component.
     *
     * @return The parent; or null if root Component.
     */
    Component parent();

    Component construct(GuiHolder holder, ViewRuntime viewManager);

    void remove(GuiHolder holder, ViewRuntime viewManager, RenderContext context);

    /**
     * Gets the width of this Component in slot count.
     *
     * @return The width in slots.
     */
    int width();

    /**
     * Gets the width of this Component in slot count.
     *
     * @return The height in slots.
     */
    int height();

    Position position();

    default int offset() {
        Component parent = parent();
        int totalOffset = 0;
        while(parent != null && parent.position().type() != Position.Type.ABSOLUTE) {
            totalOffset += parent.position().slot();
            parent = parent.parent();
        }
        return totalOffset;
    }

    default void executeForAllSlots(int positionSlot, Consumer<Integer> slotFunction) {
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                slotFunction.accept(positionSlot + j + i * (9 - width()));
            }
        }
    }

}
