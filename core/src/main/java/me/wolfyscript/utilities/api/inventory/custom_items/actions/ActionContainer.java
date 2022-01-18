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

package me.wolfyscript.utilities.api.inventory.custom_items.actions;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionContainer {

    @JsonIgnore
    private Map<NamespacedKey, ActionEvent<?>> indexedEvents;

    public ActionContainer() {
        this.indexedEvents = new HashMap<>();
    }

    @JsonSetter("events")
    public void setActionEvents(Set<ActionEvent<?>> actionEvents) {
        indexedEvents = actionEvents.stream().collect(Collectors.toMap(ActionEvent::getNamespacedKey, actionEvent -> actionEvent));
    }

    @JsonGetter("events")
    public Collection<ActionEvent<?>> getActionEvents() {
        return Collections.unmodifiableCollection(indexedEvents.values());
    }

    public <T extends ActionData> ActionEvent<T> getActionEvent(NamespacedKey key, Class<T> dataType) {
        var event = indexedEvents.get(key);
        if (event != null && event.dataType.equals(dataType)) {
            return (ActionEvent<T>) event;
        }
        return null;
    }

    public <T extends ActionData> void callActionEvent(NamespacedKey key, T data) {
        ActionEvent<T> event = getActionEvent(key, (Class<T>) data.getClass());
        if (event != null) {
            event.call(WolfyUtilCore.getInstance(), data);
        }
    }
}
