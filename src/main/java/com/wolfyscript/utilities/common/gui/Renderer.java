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

package com.wolfyscript.utilities.common.gui;

import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Renderer<T_STATE> {

    int getWidth();

    int getHeight();

    void render(T_STATE state, GuiHolder holder, RenderContext context);

    void renderComponent(T_STATE state, int slot, Component component);

    NativeRendererModule<?> getNativeModule();

    default boolean checkBoundsAtPos(int slot, Component component) throws IllegalStateException {
        int parentWidth = getWidth();
        int parentHeight = getHeight();
        return slot > 0 && slot < parentWidth * parentHeight && (slot / parentHeight) + component.width() <= parentWidth && (slot / parentWidth) + component.height() <= parentHeight;
    }

    interface ReactiveFunction <T_SIGNAL_VALUE, T_RENDERER extends Renderer<?>> {

        Pair<Integer, String> run(T_RENDERER builder, Signal.Value<?> value);

        Signal<T_SIGNAL_VALUE> getSignal();

    }

    interface Builder<T_RENDERER extends Renderer<?>> {

        <T> Signal<T> useSignal(String key, Class<T> type, Function<ComponentState, T> defaultValueFunction);

        Builder<T_RENDERER> position(int slot, String component);

        Builder<T_RENDERER> renderAt(int slot, ItemStackConfig<?> stackConfig);

        Builder<T_RENDERER> renderAt(int slot, String component);

        Builder<T_RENDERER> render(String component);

        <S> Builder<T_RENDERER> position(Signal<S> signal, Function<Signal.Value<S>, Integer> slot, Function<Signal.Value<S>, String> selector);

        <S> Builder<T_RENDERER> render(Signal<S> signal, Function<Signal.Value<S>, String> selector);

        <S> Builder<T_RENDERER> renderAt(Signal<S> signal, Function<Signal.Value<S>, Integer> slot, Function<Signal.Value<S>, String> selector);

    }

}
