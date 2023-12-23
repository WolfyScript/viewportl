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

package com.wolfyscript.utilities.eval.value_provider;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.operator.BoolOperator;

@KeyedStaticId(key = "conditioned")
public class ValueProviderConditioned<V> extends AbstractValueProvider<V> {

    private final BoolOperator condition;
    @JsonProperty("then")
    private final ValueProvider<V> thenValue;
    @JsonProperty("else")
    private final ValueProvider<V> elseValue;

    @JsonCreator
    public ValueProviderConditioned(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("condition") BoolOperator condition, @JsonProperty("then") ValueProvider<V> thenValue, @JsonProperty("else") ValueProvider<V> elseValue) {
        super(wolfyUtils);
        this.condition = condition;
        this.thenValue = thenValue;
        this.elseValue = elseValue;
    }

    @Override
    public V getValue(EvalContext context) {
        return condition.evaluate(context) ? thenValue.getValue() : elseValue.getValue();
    }
}
