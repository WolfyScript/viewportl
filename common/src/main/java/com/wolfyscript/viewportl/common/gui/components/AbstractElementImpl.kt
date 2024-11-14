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
package com.wolfyscript.viewportl.common.gui.components

import com.google.common.base.Preconditions
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.rendering.RenderPropertiesImpl
import com.wolfyscript.viewportl.gui.components.Element
import com.wolfyscript.viewportl.gui.reactivity.Effect
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import java.util.*

/**
 *
 *
 * Contains the common properties of all Components.
 * It makes it easier to create custom components.
 *
 *
 *
 * Additional functionality should be implemented on a per-component basis without further inheritance, to make it easier to expand/change in the future.
 * Instead, use interfaces (that are already there for the platform independent API) and implement them for each component.
 * Duplicate code may occur, but it can be put into static methods.
 *
 */
abstract class AbstractElementImpl<C : Element>(
    override val id: String,
    final override val viewportl: Viewportl,
    override val parent: Element?
) : Element, Effect {

    private val type: Key = Key.parse(StaticNamespacedKey.KeyBuilder.createKeyString(javaClass))
    internal var currentNodeId: Long? = null
    override var styles: RenderProperties = RenderPropertiesImpl(PropertyPosition.def())

    init {
        Preconditions.checkNotNull(
            type,
            "Missing type key! One must be provided to the Component using the annotation: ${StaticNamespacedKey::class.java.name}"
        )
    }

    override fun key(): Key {
        return type
    }

    override val nodeId: Long
        get() {
            return currentNodeId ?: -1
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractElementImpl<*>
        return type == that.type && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(type, id)
    }
}
