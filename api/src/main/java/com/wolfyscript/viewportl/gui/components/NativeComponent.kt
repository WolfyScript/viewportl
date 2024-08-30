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
package com.wolfyscript.viewportl.gui.components

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.scafall.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.scafall.config.jackson.KeyedTypeResolver;
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.Keyed
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.rendering.RenderProperties

/**
 * Native Components have a native renderer and interaction handler implementation on each platform.
 *
 * They are the only components that actually exist in the model graph (component tree).
 * The [component] function is a setup method that encapsulates and groups child components, signals, memos, etc.
 * See [component]
 *
 *
 *
 */
@JsonTypeResolver(KeyedTypeResolver::class)
@JsonTypeIdResolver(
    KeyedTypeIdResolver::class
)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonPropertyOrder(value = ["type"])
@JsonIgnoreProperties(ignoreUnknown = true)
interface NativeComponent : Keyed {

    @JsonIgnore
    override fun key(): Key

    @JsonGetter("type")
    fun type(): Key {
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
     * Gets the Viewportl instance this Component belongs to
     *
     * @return The Viewportl instance
     */
    val viewportl: Viewportl

    /**
     * The parent of this Component, or null if it is a root Component.
     *
     */
    val parent: NativeComponent?

    val styles: RenderProperties

    fun styles(config: ReceiverConsumer<RenderProperties>)

    fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long)

    fun insert(runtime: ViewRuntime, parentNode: Long)

}
