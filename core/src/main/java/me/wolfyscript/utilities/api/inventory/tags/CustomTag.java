package me.wolfyscript.utilities.api.inventory.tags;

import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashSet;
import java.util.Set;

public class CustomTag<T extends Keyed> implements Keyed {

    private final NamespacedKey namespacedKey;

    protected final Set<T> values;

    public CustomTag(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        this.values = new HashSet<>();
    }

    public Set<T> getValues() {
        return values;
    }

    public void add(T value) {
        values.add(value);
    }

    public void remove(T value) {
        values.remove(value);
    }

    public boolean contains(T value) {
        return values.contains(value);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

}
