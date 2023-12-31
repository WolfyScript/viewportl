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

import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.config.jackson.KeyedBaseType;
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
     *     The implementation may work different across platforms.<br>
     *     On plain Spigot servers, the titles do not support all components inside inventory titles, like fonts.<br>
     *     Paper fully supports all Components inside inventory titles.
     * </p>
     *
     * @return This builder to allow chaining the methods
     */
    WindowBuilder title(String staticTitle);

    WindowBuilder interact(InteractionCallback interactionCallback);

    /**
     * Specifies the constructor callback, that is called right before the component is created.
     *
     * @param render The consumer to configure the renderer
     * @return This builder for chaining
     */
    WindowBuilder construct(Consumer<WindowDynamicConstructor> render);

    Window create(Router parent);

}
