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

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.WindowRenderer;
import com.wolfyscript.utilities.common.gui.WindowType;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * Builder used to create Window Menus.<br>
 *
 */
@KeyedBaseType(baseType = ComponentBuilder.class)
public interface WindowBuilder {

    /**
     * The size of the inventory.<br>
     * This only applies when the type is not specified.<br>
     * <b>Either the type or size must be specified!</b>
     *
     * @param size The size of the inventory.
     * @return This builder to allow chaining the methods.
     */
    WindowBuilder size(int size);

    /**
     * The type of the inventory.<br>
     * When the type is specified the size is ignored.<br>
     * <b>Either the type or size must be specified!</b>
     *
     * @param type
     * @return This builder to allow chaining the methods.
     */
    WindowBuilder type(@Nullable WindowType type);

    /**
     * <p>
     *     The callback used to create the title of the inventory.
     * </p>
     * <p>
     *     The implementation may work different across platforms.<br>
     *     On plain Spigot servers, the titles do not support all components inside inventory titles, like fonts.<br>
     *     Paper fully supports all Components inside inventory titles.
     * </p>
     *
     * @param titleUpdateCallback
     * @return
     */
    WindowBuilder title(WindowTitleUpdateCallback titleUpdateCallback);

    <T> WindowBuilder createSignal(String key, Class<T> type, Consumer<Signal.Builder<T>> signalBuilder);

    WindowBuilder interact(InteractionCallback interactionCallback);

    WindowBuilder children(Consumer<WindowChildComponentBuilder> children);

    WindowBuilder render(Consumer<WindowRenderer.Builder> render);

    Window create(Router parent);

}
