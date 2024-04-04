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
package com.wolfyscript.utilities.gui.components

import com.google.common.base.Preconditions
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.Renderable
import com.wolfyscript.utilities.gui.reactivity.Effect
import com.wolfyscript.utilities.gui.rendering.RenderProperties
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
abstract class AbstractComponentImpl(
    internalID: String,
    wolfyUtils: WolfyUtils,
    parent: Component?,
    properties: RenderProperties
) : Component, Effect, Renderable {

    private val type: NamespacedKey
    private val internalID: String
    var nodeId: Long? = null
    private val wolfyUtils: WolfyUtils
    private val parent: Component?
    private val properties: RenderProperties

    init {
        Preconditions.checkNotNull(internalID)
        Preconditions.checkNotNull(wolfyUtils)
        this.type = wolfyUtils.identifiers.getNamespaced(javaClass)
        Preconditions.checkNotNull(type, "Missing type key! One must be provided to the Component using the annotation: ${KeyedStaticId::class.java.name}")
        this.internalID = internalID
        this.wolfyUtils = wolfyUtils
        this.parent = parent
        this.properties = properties
    }

    override fun key(): NamespacedKey {
        return type
    }

    override fun getID(): String {
        return internalID
    }

    override fun nodeId(): Long {
        return nodeId ?: -1
    }

    override fun getWolfyUtils(): WolfyUtils {
        return wolfyUtils
    }

    override fun parent(): Component? {
        return parent
    }

    override fun properties(): RenderProperties {
        return properties
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractComponentImpl
        return type == that.type && internalID == that.internalID
    }

    override fun hashCode(): Int {
        return Objects.hash(type, internalID)
    }
}
