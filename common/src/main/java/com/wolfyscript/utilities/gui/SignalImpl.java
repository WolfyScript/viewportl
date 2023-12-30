package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.gui.signal.Signal;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class SignalImpl<MT> implements Signal<MT> {

    private final String key;
    private final Class<MT> messageValueType;
    private MT value;
    private final GuiViewManager viewManager;
    private final Set<SignalledObject> linkedItems = new HashSet<>();

    public SignalImpl(String key, GuiViewManager viewManager, Class<MT> messageValueType, Supplier<MT> defaultValueFunction) {
        this.key = key;
        this.messageValueType = messageValueType;
        this.value = defaultValueFunction.get();
        this.viewManager = viewManager;
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
    public Class<MT> valueType() {
        return messageValueType;
    }

    @Override
    public void set(MT newValue) {
        this.value = newValue;
        ((GuiViewManagerImpl) viewManager).updateObjects(linkedItems);
    }

    @Override
    public void update(Function<MT, MT> function) {
        set(function.apply(value));
    }

    @Override
    public MT get() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalImpl<?> signal = (SignalImpl<?>) o;
        return Objects.equals(key, signal.key) && Objects.equals(messageValueType, signal.messageValueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, messageValueType);
    }

}
