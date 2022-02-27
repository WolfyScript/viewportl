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

package me.wolfyscript.utilities.util.eval.operators;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

/**
 * Represents comparison operators that compare values.<br>
 * <ul>
 *     <li>equal (==) {@link ComparisonOperatorEqual}</li>
 *     <li>not equal (!=) {@link ComparisonOperatorNotEqual}</li>
 *     <li>less (<) {@link ComparisonOperatorLess}</li>
 *     <li>less or equal(<=) {@link ComparisonOperatorLessEqual}</li>
 *     <li>greater (>) {@link ComparisonOperatorGreater}</li>
 *     <li>greater or equal (>=) {@link ComparisonOperatorGreaterEqual}</li>
 * </ul>
 *
 * @param <V> The type of the objects to compare. Must be the same for both objects.
 */
public abstract class ComparisonOperator<V extends Comparable<V>> extends BoolOperator {

    protected ValueProvider<V> thisValue;
    protected ValueProvider<V> thatValue;

    protected ComparisonOperator(NamespacedKey namespacedKey, ValueProvider<V> thisValue, ValueProvider<V> thatValue) {
        super(namespacedKey);
        this.thisValue = thisValue;
        this.thatValue = thatValue;
    }

    @Override
    public abstract boolean evaluate(EvalContext context);

}
