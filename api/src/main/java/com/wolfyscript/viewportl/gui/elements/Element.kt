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
package com.wolfyscript.viewportl.gui.elements

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.scafall.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.scafall.config.jackson.KeyedTypeResolver;
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.Keyed
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.rendering.RenderProperties

/**
 * Elements are present in the [Model][com.wolfyscript.viewportl.gui.model.ModelGraph]. They may have a native renderer and interaction handler implementation on each platform.
 * (Though not necessarily, some are only present in the Model and cannot be interacted with nor are rendered)
 *
 * Using the [component] function elements, signals, memos, etc. can be encapsulated and grouped for better code structure.
 * See [component]
 *
 */
@JsonTypeResolver(KeyedTypeResolver::class)
@JsonTypeIdResolver(
    KeyedTypeIdResolver::class
)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonPropertyOrder(value = ["type"])
@JsonIgnoreProperties(ignoreUnknown = true)
interface Element : Keyed {

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

    val nodeId: Long

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
    val parent: Element?

    val styles: RenderProperties

}
