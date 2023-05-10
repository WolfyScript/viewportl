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

package com.wolfyscript.utilities.common.gui.components;

import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RenderFunction {

    <T> Signal.Value<T> createSignal(String key, Class<T> type, Supplier<T> defaultValue);

    RenderFunction component(int i, String id);

    RenderFunction component(String id);

    <R extends RenderFunction, S> R reactive(Signal.Value<S> signal, Consumer<Signal.Value<S>> provideRenderFunction);

    RenderFunction then();

    interface Conditional {

        Conditional then(Supplier<RenderFunction> render);

        RenderFunction orElse(Supplier<RenderFunction> render);

        RenderFunction orIgnore();

    }


}
