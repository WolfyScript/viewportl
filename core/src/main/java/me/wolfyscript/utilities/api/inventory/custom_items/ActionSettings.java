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

package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.Event;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.Data;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActionSettings {

    @JsonIgnore
    private Multimap<NamespacedKey, Event<?>> indexedEvents;

    public ActionSettings() {
        this.indexedEvents = HashMultimap.create();
    }

    @JsonSetter("events")
    public void setEvents(List<Event<?>> events) {
        indexedEvents = HashMultimap.create();
        for (Event<?> event : events) {
            indexedEvents.put(event.getNamespacedKey(), event);
        }
    }

    @JsonGetter("events")
    public Collection<Event<?>> getEvents() {
        return Collections.unmodifiableCollection(indexedEvents.values());
    }

    public <T extends Data> List<Event<T>> getEvents(NamespacedKey key, Class<T> dataType) {
        return indexedEvents.get(key).stream().filter(actionEvent -> dataType.equals(actionEvent.getDataType())).map(actionEvent -> (Event<T>) actionEvent).collect(Collectors.toList());
    }

    public <T extends Data> void callEvent(NamespacedKey key, T data) {
        getEvents(key, (Class<T>) data.getClass()).forEach(event -> event.call(WolfyUtilCore.getInstance(), data));
    }
}
