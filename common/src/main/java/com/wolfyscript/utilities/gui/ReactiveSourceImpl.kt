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
import java.util.function.BiFunction;

public class ReactiveSourceImpl implements ReactiveSource {

    private final ViewRuntimeImpl viewRuntime;

    public ReactiveSourceImpl(ViewRuntimeImpl viewRuntime) {
        this.viewRuntime = viewRuntime;
    }

    @Override
    public <T> Signal<T> createSignal(T defaultValue) {
        return new SignalImpl<>(viewRuntime, defaultValue);
    }

    @Override
    public <T> Signal<T> createSignal(ReceiverFunction<ViewRuntime, T> defaultValueProvider) {
        return new SignalImpl<>(viewRuntime, defaultValueProvider.apply(viewRuntime));
    }

    @Override
    public <S, T> Signal<T> createStore(ReceiverFunction<ViewRuntime, S> storeProvider, ReceiverFunction<S, T> getter, ReceiverBiConsumer<S, T> setter) {
        return new StoreImpl<>(viewRuntime, storeProvider.apply(viewRuntime), getter, setter);
    }

    @Override
    public <T> Signal<Optional<T>> resourceSync(BiFunction<Platform, ViewRuntime, T> fetch) {
        return null;
    }

    @Override
    public <I, T> Signal<Optional<T>> resourceSync(Signal<I> input, TriFunction<Platform, ViewRuntime, I, T> fetch) {
        return null;
    }

    @Override
    public <T> Signal<Optional<T>> resourceAsync(BiFunction<Platform, ViewRuntime, T> fetch) {
        return null;
    }

    @Override
    public Effect createEffect(SerializableRunnable effect) {
        return null;
    }
}
