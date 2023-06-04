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

package com.wolfyscript.utilities.common.gui.functions;

import com.wolfyscript.utilities.common.gui.Signal;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A wrapper Consumer that is linked to {@link Signal}s.
 *
 * @param <T>
 */
public class ReactiveConsumer<T> implements Consumer<T> {

    private static int NEXT_ID = 0;

    private final int id;
    private final List<Signal<?>> signals;
    private final Consumer<T> consumer;

    public ReactiveConsumer(List<Signal<?>> signals, Consumer<T> consumer) {
        this.id = NEXT_ID++;
        this.signals = signals;
        this.consumer = consumer;
    }

    public int id() {
        return id;
    }

    public void accept(T value) {
        consumer.accept(value);
    }

    public List<Signal<?>> signals() {
        return signals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactiveConsumer<T> that = (ReactiveConsumer<T>) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
