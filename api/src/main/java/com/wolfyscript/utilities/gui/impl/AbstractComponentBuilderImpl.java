package com.wolfyscript.utilities.gui.impl;

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
