package com.wolfyscript.utilities.gui;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class SignalImpl<MT> implements com.wolfyscript.utilities.gui.signal.Signal<MT> {

    static long COUNTER = 0;

    private final long id;
    private String tagName;
    private ViewRuntimeImpl viewManager = null;
    private MT value;

    private final Set<Effect> linkedItems = new HashSet<>();

    public SignalImpl(ViewRuntimeImpl viewRuntime, MT value) {
        this.viewManager = viewRuntime;
        this.id = COUNTER++;
        this.value = value;
    }

    @Override
    public void linkTo(Effect item) { // TODO: Rethink this, move it out of the signal
        linkedItems.add(item);
    }

    @Override
    public void tagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String tagName() {
        return tagName == null || tagName.isBlank() ? ("internal_" + id) : tagName;
    }

    public long id() {
        return this.id;
    }

    @Override
    public void set(MT newValue) {
        if (viewManager != null) {
            value = newValue;
            viewManager.updateObjects(linkedItems);
        }
    }

    @Override
    public void update(Function<MT, MT> function) {
        if (viewManager != null) {
            set(function.apply(value));
        }
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
        return Objects.equals(id, signal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
