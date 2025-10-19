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
package com.wolfyscript.viewportl.common.gui.elements

import com.wolfyscript.viewportl.gui.elements.Element
import java.util.*

/**
 *
 *
 * Contains the common properties of all Elements to simplify the creation of custom elements.
 *
 * Additional functionality should be implemented on a per-element basis without further inheritance, to make it easier to expand/change in the future.
 * Instead, use interfaces (that are already there for the platform independent API) and implement them for each element.
 * Duplicate code may occur, but it can be put into static methods.
 *
 */
abstract class AbstractElementImpl<C : Element>(
    override val id: String,
) : Element {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractElementImpl<*>
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
