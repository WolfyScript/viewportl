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
package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.config.jackson.KeyedBaseType
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.functions.*
import com.wolfyscript.utilities.gui.signal.Signal
import java.util.function.Consumer

/**
 * Builder used to create Window Menus.<br></br>
 *
 */
@KeyedBaseType(baseType = ComponentBuilder::class)
interface WindowBuilder {
    /**
     * The size of the inventory.<br></br>
     * This only applies when the type is not specified.<br></br>
     * **Either the type or size must be specified!**
     *
     * @param size The size of the inventory.
     * @return This builder to allow chaining the methods.
     */
    fun size(size: Int): WindowBuilder

    /**
     * The type of the inventory.<br></br>
     * When the type is specified the size is ignored.<br></br>
     * **Either the type or size must be specified!**
     *
     * @param type
     * @return This builder to allow chaining the methods.
     */
    fun type(type: WindowType): WindowBuilder

    /**
     *
     *
     * The implementation may work different across platforms.<br></br>
     * On plain Spigot servers, the titles do not support all components inside inventory titles, like fonts.<br></br>
     * Paper fully supports all Components inside inventory titles.
     *
     *
     * @return This builder to allow chaining the methods
     */
    fun title(staticTitle: String): WindowBuilder

    fun interact(interactionCallback: InteractionCallback): WindowBuilder

    /**
     * Specify a dynamic custom title supplier that is used to update the title of the Inventory.<br></br>
     *
     *
     * Any signal used inside the supplier will cause it to update when the signal is updated.
     *
     *
     * @param titleSupplier The supplier that provides the new title of the inventory
     * @return This builder for chaining
     */
    fun title(titleSupplier: SerializableSupplier<net.kyori.adventure.text.Component>): WindowBuilder

    /**
     * When no dynamic title is used (see [.title]), then this method can be used to
     * create placeholder resolvers for the static title. The static title can be specified using [WindowBuilder.title].
     *
     *
     * The title is updated whenever any of the specified signals are updated.
     *
     *
     *
     *
     * The placeholder will be equal to the key of the signal.<br></br>
     * e.g. <br></br>
     * <pre>createSignal("count", () -> 0)</pre> will provide a placeholder <pre>&lt;count&gt;</pre>
     *
     *
     *
     * @param signals The signals to use as placeholders
     * @return This builder for chaining
     */
    fun titleSignals(vararg signals: Signal<*>): WindowBuilder

    /**
     * Adds a task that is run periodically while the Window is open.
     *
     * @param runnable The task to run, may update signals
     * @param intervalInTicks The interval for the task in ticks
     * @return This builder for chaining
     */
    fun addIntervalTask(runnable: Runnable, intervalInTicks: Long): WindowBuilder

    /**
     *
     *
     * Constructs a reactive function to dynamically render components.<br></br>
     * This is only recommended for complex methods, like if switches are required.<br></br>
     * If a simple condition is enough [.conditionalComponent] should be used instead!
     *
     *
     *
     * The callback is updated whenever a signal used inside it is updated.
     *
     *
     * @param reactiveFunction The function to run on signal updates.
     * @return This builder for chaining.
     */
    fun reactive(reactiveFunction: SignalableReceiverFunction<ReactiveRenderBuilder, ReactiveResult?>): WindowBuilder

    /**
     *
     *
     * Renders the specified component whenever the condition is met.<br></br>
     * Any signal used inside the condition will cause it to update when the signal is updated.
     *
     *
     *
     * The specified component is constructed upon invocation of this method and simply rendered/removed whenever the condition changes.<br></br>
     * Further updates to the components need to be handled by using signals.
     *
     *
     * @param condition       The condition that is reactive to signals used inside it.
     * @param id              The id of the component to render.
     * @param builderType     The type of builder to use.
     * @param builderConsumer The consumer to configure the builder.
     * @param <B>             The type of the Component Builder
     * @return This builder for chaining
    </B> */
    fun <B : ComponentBuilder<out Component?, Component?>> conditionalComponent(
        condition: SerializableSupplier<Boolean>,
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder

    fun <BV : ComponentBuilder<out Component?, Component?>, BI : ComponentBuilder<out Component?, Component?>> renderWhenElse(
        condition: SerializableSupplier<Boolean>,
        builderValidType: Class<BV>,
        builderValidConsumer: Consumer<BV>,
        builderInvalidType: Class<BI>,
        builderInvalidConsumer: SignalableReceiverConsumer<BI>
    ): WindowBuilder

    /**
     *
     *
     * Renders the specified component with the given id.
     *
     *
     * @param id              The id of the component to render
     * @param builderType     The type of the builder to use
     * @param builderConsumer The consumer to configure the builder
     * @param <B>             The type of the component builder
     * @return This Builder for chaining
    </B> */
    fun <B : ComponentBuilder<out Component?, Component?>> component(
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder

    /**
     * Renders the specified component at the given position.
     * This is useful if the position wasn't specified by a config file
     *
     * @param position
     * @param id
     * @param builderType
     * @param builderConsumer
     * @return
     * @param <B>
    </B> */
    fun <B : ComponentBuilder<out Component?, Component?>> component(
        position: Position,
        id: String,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder

    fun <B : ComponentBuilder<out Component?, Component?>> component(
        position: Position,
        builderType: Class<B>,
        builderConsumer: SignalableReceiverConsumer<B>
    ): WindowBuilder

    fun create(parent: Router): Window
}
