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

package com.wolfyscript.utilities.gui.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.ComponentBuilder;
import com.wolfyscript.utilities.gui.Position;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.config.jackson.KeyedBaseType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@KeyedBaseType(baseType = ComponentBuilder.class)
public abstract class AbstractComponentBuilderImpl<OWNER extends Component, PARENT extends Component> implements ComponentBuilder<OWNER, PARENT> {

    @JsonProperty("type")
    private final NamespacedKey type;
    private final String id;
    @JsonProperty("position")
    private final Position position;
    @JsonIgnore
    private final Set<Signal<?>> signals = new HashSet<>();
    private final WolfyUtils wolfyUtils;

    protected AbstractComponentBuilderImpl(String id, WolfyUtils wolfyUtils, Position position) {
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        this.id = id;
        this.wolfyUtils = wolfyUtils;
        this.position = position;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Position position() {
        return position;
    }

    protected void addSignals(Collection<Signal<?>> signals) {
        this.signals.addAll(signals);
    }

    @Override
    public Set<Signal<?>> signals() {
        return signals;
    }

    protected WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

}
