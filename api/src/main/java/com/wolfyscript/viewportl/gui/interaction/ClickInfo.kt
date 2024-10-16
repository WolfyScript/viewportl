/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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

package com.wolfyscript.viewportl.gui.interaction

import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface ClickInfo : InteractionTransaction {

    val slot: Int

    val cursorStack : ItemStack?

    val currentStack : ItemStack?

    /**
     * An inventory click with the primary mouse button
     */
    interface Primary : ClickInfo

    /**
     * A double click with the primary mouse button
     */
    interface Double : Primary

    /**
     * An inventory click with the secondary mouse button
     */
    interface Secondary : ClickInfo

    /**
     * An inventory click with the middle mouse button
     */
    interface Middle : ClickInfo

    /**
     * An inventory click with the number key (0-9) that swaps item with the associated hotbar slot
     */
    interface NumberPress : ClickInfo {
        val number: Int
    }

    /**
     * An inventory click while the shift key is pressed
     */
    interface Shift : ClickInfo {

        /**
         * An inventory click with the primary mouse button, while shift is pressed
         */
        interface Primary : Shift, ClickInfo.Primary

        /**
         * An inventory click with the secondary mouse button, while shift is pressed
         */
        interface Secondary : Shift, ClickInfo.Secondary

    }

    /**
     * Dropping an item from within an open container
     */
    interface Drop : ClickInfo {

        /**
         * Click on the outside of the open container
         */
        interface Outside : Drop {

            /**
             * Click on the outside of the open container with the primary mouse button
             */
            interface Primary : Outside, ClickInfo.Primary

            /**
             * Click on the outside of the open container with the secondary mouse button
             */
            interface Secondary : Outside, ClickInfo.Secondary
        }

        /**
         * Dropping the whole stack (including stacks of a single item) by hovering over it and pressing CTRL+Q
         */
        interface Full : Drop

        /**
         * Dropping a single item by hovering over the item and pressing Q
         */
        interface Single : Drop

    }

    interface Drag : ClickInfo {

        /**
         *
         */
        interface Primary : Drag, ClickInfo.Primary

        /**
         *
         */
        interface Secondary : Drag, ClickInfo.Secondary

        /**
         *
         */
        interface Middle : Drag, ClickInfo.Middle

    }
    
    interface Creative : ClickInfo {}

}
