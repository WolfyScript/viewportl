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
package com.wolfyscript.viewportl.gui.components

import com.google.common.base.Preconditions
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.viewportl.gui.reactivity.Effect
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import com.wolfyscript.viewportl.gui.rendering.RenderPropertiesImpl
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
abstract class AbstractComponentImpl<C : Component>(
    override val id: String,
    final override val wolfyUtils: WolfyUtils,
    override val parent: Component?
) : Component, Effect {

    private val type: NamespacedKey = wolfyUtils.identifiers.getNamespaced(javaClass)
    var nodeId: Long? = null
    override var styles: RenderProperties = RenderPropertiesImpl(PropertyPosition.def())

    init {
        Preconditions.checkNotNull(type, "Missing type key! One must be provided to the Component using the annotation: ${KeyedStaticId::class.java.name}")
    }

    override fun key(): NamespacedKey {
        return type
    }

    override fun nodeId(): Long {
        return nodeId ?: -1
    }

    override fun styles(config: ReceiverConsumer<RenderProperties>) {
        with(config) {
            styles.consume()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractComponentImpl<C>
        return type == that.type && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(type, id)
    }
}
