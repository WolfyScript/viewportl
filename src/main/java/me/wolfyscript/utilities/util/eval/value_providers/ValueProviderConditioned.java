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

package me.wolfyscript.utilities.util.eval.value_providers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;

public class ValueProviderConditioned<V> extends AbstractValueProvider<V> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("conditioned");

    private final BoolOperator condition;
    @JsonProperty("then")
    private final ValueProvider<V> thenValue;
    @JsonProperty("else")
    private final ValueProvider<V> elseValue;

    @JsonCreator
    public ValueProviderConditioned(@JsonProperty("condition") BoolOperator condition, @JsonProperty("then") ValueProvider<V> thenValue, @JsonProperty("else") ValueProvider<V> elseValue) {
        super(KEY);
        this.condition = condition;
        this.thenValue = thenValue;
        this.elseValue = elseValue;
    }

    @Override
    public V getValue(EvalContext context) {
        return condition.evaluate(context) ? thenValue.getValue() : elseValue.getValue();
    }
}
