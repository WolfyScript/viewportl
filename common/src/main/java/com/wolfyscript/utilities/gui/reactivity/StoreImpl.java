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

package com.wolfyscript.utilities.gui.reactivity;

import com.wolfyscript.utilities.gui.ViewRuntime;
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer;
import com.wolfyscript.utilities.gui.functions.ReceiverFunction;

import java.util.function.Function;

public class StoreImpl<S, V> implements Store<V> {

    private final long id;
    private final ViewRuntime viewManager;
    private final ReceiverFunction<S, V> getter;
    private final ReceiverBiConsumer<S, V> setter;
    private final S store;
    private String tagName;

    public StoreImpl(long id, ViewRuntime viewManager, S store, ReceiverFunction<S, V> getter, ReceiverBiConsumer<S, V> setter) {
        this.id = id;
        this.viewManager = viewManager;
        this.getter = getter;
        this.setter = setter;
        this.store = store;
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
