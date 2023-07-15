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

import com.wolfyscript.utilities.common.gui.functions.SerializableConsumer;
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
    WindowBuilder construct(Consumer<WindowRenderer.Builder> render);

    /**
     * <p>
     *     Initializes the specified component with the given id at the given slot.<br>
     *     <b>The component won't yet be constructed nor rendered!</b>
     *     <p>
     *         This is useful if the component has some static parts that are non-reactive.<br>
     *         <i>This is used when loading the components from the config files (.conf)</i><br>
     *         They can then be extended and rendered inside the {@link #construct(Consumer)} callback.
     *     </p>
     * </p>
     * <p>
     *     In case you need to have reactive components, create them inside the {@link #construct(Consumer)} callback.
     * </p>
     * @param id                The id of the component to render
     * @param builderType       The type of the builder to use
     * @param builderConsumer   The consumer to configure the builder
     * @return This Builder for chaining
     * @param <B> The type of the component builder
     */
    <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder init(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);

    /**
     * <p>
     *     Renders the specified <b>static</b> component with the given id.
     * </p>
     * <p>
     *     Static components are constructed directly and are not recreated per {@link GuiViewManager}.
     *     Therefor they improve performance, as they are only created once.<br>
     *     <b>Static components cannot use signals!</b>
     * </p>
     * <p>
     *     In case you need to have reactive components, create them inside the {@link #construct(Consumer)} callback.
     * </p>
     * @param id                The id of the component to render
     * @param builderType       The type of the builder to use
     * @param builderConsumer   The consumer to configure the builder
     * @return This Builder for chaining
     * @param <B> The type of the component builder
     */
    <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder render(String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);

    /**
     * <p>
     *     Initializes the specified component with the given id at the given slot, and renders it statically.<br>
     *     <b>It basically combines <br>
     *     {@link #init(int, String, Class, SerializableConsumer)} and <br>
     *     {@link #render(String, Class, SerializableConsumer)}</b>
     *
     *     <p>
     *         This is only really useful if you need to define positions of components in code.<br>
     *         Usually you should use the config files (.conf), to specify slots.
     *     </p>
     * </p>
     * <p>
     *     Static components are constructed directly and are not recreated per {@link GuiViewManager}.
     *     Therefor they improve performance, as they are only created once.<br>
     *     <b>Static components cannot use signals!</b>
     * </p>
     * <p>
     *     In case you need to have reactive components, create them inside the {@link #construct(Consumer)} callback.
     * </p>
     *
     * @param slot
     * @param id
     * @param builderType
     * @param builderConsumer
     * @return
     * @param <B>
     */
    <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder renderAt(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);

    Window create(Router parent);

}
