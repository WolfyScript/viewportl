package com.wolfyscript.utilities.bukkit.adapters;

public abstract class BukkitRefAdapter<T> {

    protected final T bukkitRef;

    protected BukkitRefAdapter(T bukkitRef) {
        this.bukkitRef = bukkitRef;
    }

    public T getBukkitRef() {
        return bukkitRef;
    }
}
