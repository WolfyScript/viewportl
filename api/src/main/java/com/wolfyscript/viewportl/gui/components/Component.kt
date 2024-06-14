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

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.utilities.Keyed
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.viewportl.gui.rendering.RenderProperties


@JsonTypeResolver(KeyedTypeResolver::class)
@JsonTypeIdResolver(
    KeyedTypeIdResolver::class
)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonPropertyOrder(value = ["type"])
@JsonIgnoreProperties(ignoreUnknown = true)
interface Component : Keyed {

    @JsonIgnore
    override fun key(): NamespacedKey

    @JsonGetter("type")
    fun type(): NamespacedKey {
        return key()
    }

    /**
     * Gets the unique id (in context of the parent) of this component.
     *
     * @return The id of this component.
     */
    val id: String?

    fun nodeId(): Long

    /**
     * Gets the global WolfyUtils instance, this component belongs to.
     *
     * @return The WolfyUtils API instance.
     */
    val wolfyUtils: WolfyUtils

    /**
     * The parent of this Component, or null if it is a root Component.
     *
     */
    val parent: Component?

    /**
     * Gets the width of this Component in slot count.
     *
     * @return The width in slots.
     */
    fun width(): Int

    /**
     * Gets the width of this Component in slot count.
     *
     * @return The height in slots.
     */
    fun height(): Int

    val properties: RenderProperties

    fun properties(config: ReceiverConsumer<RenderProperties>)

    fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long)

    fun insert(runtime: ViewRuntime, parentNode: Long)

    fun completeBuild() { }
}
