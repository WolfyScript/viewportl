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

package com.wolfyscript.viewportl.common.gui.inventoryui.rendering

import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.rendering.RenderContext

abstract class InvUIRenderContext : RenderContext {

    abstract val renderer: InvUIRenderer<*, *>
    private var currentNode: Element? = null
    private var slotOffsetToParent = 0

    fun setSlotOffset(offset: Int) {
        this.slotOffsetToParent = offset
    }

    override fun currentOffset(): Int {
        return slotOffsetToParent
    }

    override fun enterNode(element: Element) {
        this.currentNode = element
    }

    override fun exitNode() {
        this.currentNode = null
    }

    override val currentElement: Element?
        get() = currentNode
}
