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
package com.wolfyscript.utilities.eval.value_provider

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.eval.context.EvalContext
import com.wolfyscript.utilities.eval.operator.BoolOperator

@KeyedStaticId(key = "conditioned")
class ValueProviderConditioned<V> @JsonCreator constructor(
    @param:JsonProperty(
        "condition"
    ) private val condition: BoolOperator,
    @field:JsonProperty("then") @param:JsonProperty("then") private val thenValue: ValueProvider<V>,
    @field:JsonProperty(
        "else"
    ) @param:JsonProperty(
        "else"
    ) private val elseValue: ValueProvider<V>
) : AbstractValueProvider<V>() {
    override fun getValue(context: EvalContext?): V {
        return if (condition.evaluate(context)) thenValue.value else elseValue.value
    }
}
