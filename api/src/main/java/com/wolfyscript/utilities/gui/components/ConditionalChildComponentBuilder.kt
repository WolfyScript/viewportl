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

package com.wolfyscript.utilities.gui.components

import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableSupplier

interface ConditionalChildComponentBuilder<T> {

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
    infix fun whenever(condition: SerializableSupplier<Boolean>): When<T>

    interface When<T> {

        infix fun then(builderConsumer: ReceiverConsumer<ComponentGroupBuilder>) : Else<T>

    }

    interface Else<T> {

        infix fun orElse(builderConsumer: ReceiverConsumer<ComponentGroupBuilder>) : T

        fun elseNone() : T

    }

}