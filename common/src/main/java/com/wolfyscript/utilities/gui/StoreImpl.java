package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.gui.signal.Store;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class StoreImpl<V> implements Store<V> {

    private final String key;
    private final Class<V> messageValueType;
    private final GuiViewManager viewManager;
    private final Supplier<V> getValue;
    private final Consumer<V> setValue;
    private final Set<SignalledObject> linkedItems = new HashSet<>();

    public StoreImpl(String key, GuiViewManager viewManager, Class<V> messageValueType, Supplier<V> getValue, Consumer<V> setValue) {
        this.key = key;
        this.messageValueType = messageValueType;
        this.viewManager = viewManager;
        this.getValue = getValue;
        this.setValue = setValue;
    }

    @Override
    public void linkTo(SignalledObject item) {
        linkedItems.add(item);
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Class<V> valueType() {
        return this.messageValueType;
    }

    @Override
    public void set(V newValue) {
        setValue.accept(newValue);
        ((GuiViewManagerImpl) viewManager).updateObjects(linkedItems);
    }

    @Override
    public void update(Function<V, V> function) {
        set(function.apply(get()));
    }

    @Override
    public V get() {
        return getValue.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreImpl<?> signal = (StoreImpl<?>) o;
        return Objects.equals(key, signal.key) && Objects.equals(messageValueType, signal.messageValueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, messageValueType);
    }
}
