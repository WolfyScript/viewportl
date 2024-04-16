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

package com.wolfyscript.utilities.bukkit.world.items.actions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver;
import com.wolfyscript.utilities.config.jackson.OptionalKeyReference;
import java.util.List;
import java.util.Objects;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key"})
@OptionalKeyReference(serializeAsKey = false, registryKey = "wolfyutilities:custom_item/events/values")
public abstract class Event<T extends Data> implements Keyed {

    @JsonIgnore
    protected WolfyUtils wolfyUtils;
    private final NamespacedKey key;
    @JsonIgnore
    protected final Class<T> dataType;
    private List<Action<? super T>> actions;

    protected Event(WolfyUtils wolfyUtils, NamespacedKey key, Class<T> dataType) {
        this.key = key;
        this.wolfyUtils = wolfyUtils;
        this.dataType = dataType;
    }

    public void call(WolfyUtils wolfyUtils1, T data) {
        for (Action<? super T> action : actions) {
            action.execute(wolfyUtils, data);
        }
    }

    @JsonIgnore
    public boolean isApplicable(Action<?> action) {
        return action.dataType.isAssignableFrom(dataType);
    }

    public void setActions(List<Action<? super T>> actions) {
        this.actions = actions.stream().filter(this::isApplicable).toList();
    }

    public List<Action<? super T>> getActions() {
        return actions;
    }

    @JsonIgnore
    public Class<T> getDataType() {
        return dataType;
    }

    @JsonIgnore
    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?> that = (Event<?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(key);
    }
}
