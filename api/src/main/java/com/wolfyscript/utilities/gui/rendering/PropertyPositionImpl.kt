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

package com.wolfyscript.utilities.gui.rendering

internal class PropertyPositionImpl(
    private val slotPositioning: PropertyPosition.SlotPositioning? = null,
    private val pixelPositionImpl: PropertyPixelPositionImpl? = null
) : PropertyPosition {

    override fun slotPositioning(): PropertyPosition.SlotPositioning? = slotPositioning

    override fun pixelPositioning(): PropertyPosition.PixelPositioning? = pixelPositionImpl

    class PropertySlotPositionImpl(private val slot: Int) : PropertyPosition.SlotPositioning {

        override fun slot() : Int = slot

    }

    class PropertyPixelPositionImpl(
        private val left: Int? = null,
        private val right: Int? = null,
        private val top: Int? = null,
        private val bottom: Int? = null,
        private val type: PropertyPosition.PixelPositioning.Type = PropertyPosition.PixelPositioning.Type.DEFAULT
    ) : PropertyPosition.PixelPositioning {

        override fun left(): Int? = left

        override fun right(): Int? = right

        override fun top(): Int? = top

        override fun bottom(): Int? = bottom

        override fun type(): PropertyPosition.PixelPositioning.Type = type

    }
}
