package com.wolfyscript.utilities.common.gui.impl;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@KeyedBaseType(baseType = ComponentBuilder.class)
public abstract class AbstractComponentBuilderImpl<OWNER extends Component, PARENT> implements ComponentBuilder<OWNER, PARENT> {

    @JsonProperty("type")
    private final NamespacedKey type;
    private final String id;
    @JsonProperty("slots")
    @JsonAlias("slot")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private final IntList slots;
    @JsonIgnore
    private final Set<Signal<?>> signals = new HashSet<>();
    private final WolfyUtils wolfyUtils;

    protected AbstractComponentBuilderImpl(String id, WolfyUtils wolfyUtils, IntList slots) {
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        this.id = id;
        this.wolfyUtils = wolfyUtils;
        this.slots = slots;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public IntList getSlots() {
        return slots;
    }

    protected void addSignals(Collection<Signal<?>> signals) {
        this.signals.addAll(signals);
    }

    @Override
    public Set<Signal<?>> getSignals() {
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
