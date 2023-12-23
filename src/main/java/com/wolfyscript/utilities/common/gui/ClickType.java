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

public enum ClickType {

    /**
     * Pressing the primary mouse button.
     */
    PRIMARY,
    /**
     * Holding shift and pressing the primary mouse button.
     */
    SHIFT_PRIMARY,
    /**
     * Pressing the secondary mouse button.
     */
    SECONDARY,
    /**
     * Holding shift and pressing the secondary mouse button.
     */
    SHIFT_SECONDARY,
    /**
     * Clicking the primary mouse button on the grey area around the container.
     */
    CONTAINER_BORDER_PRIMARY,
    /**
     * Clicking the secondary mouse button on the grey area around the container.
     */
    CONTAINER_BORDER_SECONDARY,
    /**
     * Pressing the middle mouse button.
     */
    MIDDLE,
    /**
     * One of the number keys 1-9, correspond to slots on the hotbar.
     */
    NUMBER_KEY,
    /**
     * Pressing the primary mouse button twice in quick succession.
     */
    DOUBLE_CLICK,
    /**
     * The "Drop" key (defaults to Q).
     */
    DROP,
    /**
     * Holding Ctrl while pressing the "Drop" key (defaults to Q).
     */
    CONTROL_DROP,
    /**
     * Any action done with the Creative inventory open.
     */
    CREATIVE,

}
