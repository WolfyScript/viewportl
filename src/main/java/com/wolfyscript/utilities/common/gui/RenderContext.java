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

import com.wolfyscript.utilities.common.items.ItemStackConfig;

/**
 * The data that contains all the information needed to render the Menu.
 *
 */
public interface RenderContext {

    Component getCurrentComponent();

    int currentOffset();

    void setStack(int slot, ItemStackConfig<?> stackConfig);

    void setNativeStack(int slot, Object nativeStack);

    default boolean checkIfSlotInBounds(int slot) {
        int outerWidth;
        int outerHeight;
        if (getCurrentComponent().parent() != null) {
            Component parent = getCurrentComponent().parent();
            outerWidth = parent.width();
            outerHeight = parent.height();
        } else {
            outerWidth = 9;
            outerHeight = 6;
        }
        if (slot >= 0 && slot < outerWidth * outerHeight) {
            return true;
        }
        throw new IllegalArgumentException("Slot " + slot + " out of bounds! Must be in the range of [" + 0 + "..." + (outerWidth * outerHeight - 1) + "] !");
    }

}
