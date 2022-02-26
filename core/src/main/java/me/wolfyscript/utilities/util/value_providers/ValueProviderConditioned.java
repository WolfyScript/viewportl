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

package me.wolfyscript.utilities.util.value_providers;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.context.EvalContext;
import me.wolfyscript.utilities.util.operators.BoolOperator;
import me.wolfyscript.utilities.util.operators.Operator;
import me.wolfyscript.utilities.util.value_comparators.ValueComparator;

public class ValueProviderConditioned<V> extends AbstractValueProvider<V> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("value_providers/conditioned");

    private final BoolOperator condition;
    private final ValueProvider<V> thenValue;
    private final ValueProvider<V> elseValue;

    protected ValueProviderConditioned(BoolOperator condition, ValueProvider<V> thenValue, ValueProvider<V> elseValue) {
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
