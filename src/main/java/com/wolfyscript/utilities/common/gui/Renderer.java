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

import java.util.Map;
import java.util.function.Supplier;

public interface Renderer {

    int getWidth();

    int getHeight();

    void render(GuiHolder holder, RenderContext context);

    Map<String, Signal<?>> getSignals();

    NativeRendererModule<?> getNativeModule();

    default boolean checkBoundsAtPos(int slot, Component component) throws IllegalStateException {
        int parentWidth = getWidth();
        int parentHeight = getHeight();
        return slot > 0 && slot < parentWidth * parentHeight && (slot / parentHeight) + component.width() <= parentWidth && (slot / parentWidth) + component.height() <= parentHeight;
    }

    interface Builder<T_RENDERER extends Renderer> {

        <T> Signal<T> createSignal(String key, Class<T> type, Supplier<T> defaultValueFunction);

    }

}
