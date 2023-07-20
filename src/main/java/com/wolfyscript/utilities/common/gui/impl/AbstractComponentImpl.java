package com.wolfyscript.utilities.common.gui.impl;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.SignalledObject;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Objects;

/**
 * <p>
 * Contains the common properties of all Components.
 * It makes it easier to create custom components.
 * </p>
 * <p>
 * Additional functionality should be implemented on a per-component basis without further inheritance, to make it easier to expand/change in the future.
 * Instead, use interfaces (that are already there for the platform independent API) and implement them for each component.
 * Duplicate code may occur, but it can be put into static methods.
 * </p>
 */
public abstract class AbstractComponentImpl implements Component, SignalledObject {

    private final NamespacedKey type;
    private final String internalID;
    private final WolfyUtils wolfyUtils;
    private final Component parent;
    private final IntList slots;

    public AbstractComponentImpl(String internalID, WolfyUtils wolfyUtils, Component parent, IntList slots) {
        Preconditions.checkNotNull(internalID);
        Preconditions.checkNotNull(wolfyUtils);
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        Preconditions.checkNotNull(type, "Missing type key! One must be provided to the Component using the annotation: %s", KeyedStaticId.class.getName());
        this.internalID = internalID;
        this.wolfyUtils = wolfyUtils;
        this.parent = parent;
        this.slots = slots;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    @Override
    public String getID() {
        return internalID;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Component parent() {
        return parent;
    }

    public IntList getSlots() {
        return slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractComponentImpl that = (AbstractComponentImpl) o;
        return Objects.equals(type, that.type) && Objects.equals(internalID, that.internalID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, internalID);
    }
}
