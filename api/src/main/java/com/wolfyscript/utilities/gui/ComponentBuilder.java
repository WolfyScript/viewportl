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

package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver;

import java.util.Set;


@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonPropertyOrder(value = { "type" })
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ComponentBuilder<COMPONENT extends Component, PARENT extends Component> extends Keyed {

    @JsonIgnore
    @Override
    NamespacedKey getNamespacedKey();

    default NamespacedKey getType() {
        return getNamespacedKey();
    }

    String id();

    Position position();

    ComponentBuilder<COMPONENT, PARENT> position(Position position);

    /**
     * Gets the signals that this component builder uses inside the parent construction consumer.
     *
     * @return The signals used in this builder.
     */
    Set<Signal<?>> signals();

    COMPONENT create(PARENT parent);

}
