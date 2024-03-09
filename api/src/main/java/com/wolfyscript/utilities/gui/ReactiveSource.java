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

package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer;
import com.wolfyscript.utilities.gui.functions.ReceiverFunction;
import com.wolfyscript.utilities.gui.functions.SerializableRunnable;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.platform.Platform;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.*;

public interface ReactiveSource {

    <T> Signal<T> createSignal(T defaultValue);

    <T> Signal<T> createSignal(ReceiverFunction<ViewRuntime, T> defaultValueProvider);

    /**
     * Creates a Signal with a value, which is stored externally of the GUI.
     *
     * @param storeProvider The data storage where the data exists
     * @param supplier The value getter
     * @param consumer The value setter
     * @return The signal
     * @param <S>
     * @param <T> The type of the value
     */
    <S, T> Signal<T> createStore(ReceiverFunction<ViewRuntime, S> storeProvider, ReceiverFunction<S, T> supplier, ReceiverBiConsumer<S, T> consumer);

    /**
     * Must be used to fetch data from the main Minecraft thread (i.e. Entities, World, etc.).
     * This is because the GUI is run async on a different thread!
     * <p>
     *     After creation, it runs the specified function on the main thread and fetches the data.
     *     The signal gets updated when the data is available.
     *     When the signal value is requested before the data is available it returns an empty Optional.
     * </p>
     *
     * @param fetch The function to run on the main thread.
     * @return A signal that contains an Optional wrapping the fetched data; empty by default; non-empty when data has been fetched
     * @param <T> The type of the value
     */
    <T> Signal<Optional<T>> resourceSync(BiFunction<Platform, ViewRuntime, T> fetch);

    <I, T> Signal<Optional<T>> resourceSync(Signal<I> input, TriFunction<Platform, ViewRuntime, I, T> fetch);

    /**
     * May be used to fetch data async.
     *
     * <p>
     *     After creation, it runs the specified function async and fetches the data.
     *     The signal gets updated when the data is available.
     *     When the signal value is requested before the data is available it returns an empty Optional.
     * </p>
     *
     * @param fetch The function to run async
     * @return A signal that contains an Optional wrapping the fetched data; empty by default; non-empty when data has been fetched
     * @param <T> The type of the value
     */
    <T> Signal<Optional<T>> resourceAsync(BiFunction<Platform, ViewRuntime, T> fetch);

    Effect createEffect(SerializableRunnable effect);

}
