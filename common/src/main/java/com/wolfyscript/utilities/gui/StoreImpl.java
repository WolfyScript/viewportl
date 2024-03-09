package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer;
import com.wolfyscript.utilities.gui.functions.ReceiverFunction;
import com.wolfyscript.utilities.gui.signal.Store;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StoreImpl<S, V> implements Store<V> {

    private final long id;
    private final ViewRuntime viewManager;
    private final ReceiverFunction<S, V> getter;
    private final ReceiverBiConsumer<S, V> setter;
    private final Set<Effect> linkedItems = new HashSet<>();
    private final S store;
    private String tagName;

    public StoreImpl(ViewRuntime viewManager, S store, ReceiverFunction<S, V> getter, ReceiverBiConsumer<S, V> setter) {
        this.id = SignalImpl.COUNTER++;
        this.viewManager = viewManager;
        this.getter = getter;
        this.setter = setter;
        this.store = store;
    }

    @Override
    public void linkTo(Effect item) {
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

    @Override
    public void set(V newValue) {
        setter.consume(store, newValue);
        ((ViewRuntimeImpl) viewManager).updateObjects(linkedItems);
    }

    @Override
    public void update(Function<V, V> function) {
        set(function.apply(get()));
    }

    @Override
    public V get() {
        return getter.apply(store);
    }

}
