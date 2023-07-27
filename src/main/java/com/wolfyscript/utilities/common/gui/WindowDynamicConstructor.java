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
import com.wolfyscript.utilities.common.gui.functions.SerializableFunction;
import com.wolfyscript.utilities.common.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.common.gui.signal.Signal;

import java.util.function.Consumer;

public interface WindowDynamicConstructor extends DynamicConstructor {

    /**
     * Specify a dynamic custom title supplier that is used to update the title of the Inventory.<br>
     * <p>
     * Any signal used inside the supplier will cause it to update when the signal is updated.
     * </p>
     *
     * @param titleSupplier The supplier that provides the new title of the inventory
     * @return This builder for chaining
     */
    WindowDynamicConstructor title(SerializableSupplier<net.kyori.adventure.text.Component> titleSupplier);

    /**
     * When no dynamic title is used (see {@link #title(SerializableSupplier)}), then this method can be used to
     * create placeholder resolvers for the static title. The static title can be specified using {@link WindowBuilder#title(String)}.
     * <p>
     *     The title is updated whenever any of the specified signals are updated.
     * </p>
     *
     * <p>
     * The placeholder will be equal to the key of the signal.<br>
     * e.g. <br>
     * <pre>createSignal("count", () -> 0)</pre> will provide a placeholder <pre>&lt;count&gt;</pre>
     *
     * </p>
     *
     * @param signals The signals to use as placeholders
     * @return This builder for chaining
     */
    WindowDynamicConstructor titleSignals(Signal<?>... signals);

    /**
     * <p>
     *     Constructs a reactive function to dynamically render components.<br>
     *     This is only recommended for complex methods like if switches are required.<br>
     *     If a simple condition is enough {@link #ifThenRender(SerializableSupplier, String, Class, SerializableConsumer)} should be used instead!
     * </p>
     * <p>
     *     The callback is updated whenever a signal used inside it is updated.
     * </p>
     *
     * @param reactiveFunction The function to run on signal updates.
     * @return This builder for chaining.
     */
    WindowDynamicConstructor reactive(SerializableFunction<ReactiveRenderBuilder, ReactiveRenderBuilder.ReactiveResult> reactiveFunction);

    /**
     * <p>
     *     Renders the specified component whenever the condition is met.<br>
     *     Any signal used inside the condition will cause it to update when the signal is updated.
     * </p>
     * <p>
     *     The specified component is constructed upon invocation of this method and simply rendered/removed whenever the condition changes.<br>
     *     Further updates to the components need to be handled by using signals.
     * </p>
     *
     * @param condition       The condition that is reactive to signals used inside it.
     * @param id              The id of the component to render.
     * @param builderType     The type of builder to use.
     * @param builderConsumer The consumer to configure the builder.
     * @param <B>             The type of the Component Builder
     * @return This builder for chaining
     */
    <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructor ifThenRender(SerializableSupplier<Boolean> condition, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);

    <BV extends ComponentBuilder<? extends Component, Component>, BI extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructor ifThenRenderOr(SerializableSupplier<Boolean> condition, Class<BV> builderValidType, Consumer<BV> builderValidConsumer, Class<BI> builderInvalidType, SerializableConsumer<BI> builderInvalidConsumer);

    /**
     *
     *
     * @param slot
     * @param id
     * @param builderType
     * @param builderConsumer
     * @return
     * @param <B>
     */
    <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructor position(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);

    /**
     * <p>
     *     Renders the specified component with the given id.
     * </p>
     *
     * @param id              The id of the component to render
     * @param builderType     The type of the builder to use
     * @param builderConsumer The consumer to configure the builder
     * @param <B>             The type of the component builder
     * @return This Builder for chaining
     */
    <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructor render(String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);

    <B extends ComponentBuilder<? extends Component, Component>> WindowDynamicConstructor renderAt(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer);
}
