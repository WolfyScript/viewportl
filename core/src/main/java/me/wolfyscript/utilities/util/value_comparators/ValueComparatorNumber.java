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

package me.wolfyscript.utilities.util.value_comparators;

import me.wolfyscript.utilities.util.value_providers.ValueProvider;

public abstract class ValueComparatorNumber<I extends Number & Comparable<I>> extends ValueComparator<I> {

    private final ValueComparatorNumber.Operator operator;

    public ValueComparatorNumber(ValueProvider<I> valueThis, ValueProvider<I> valueThat, Operator operator) {
        super(valueThis, valueThat);
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    protected boolean evaluateByOrder(int value) {
        return switch (getOperator()) {
            case EQUALS -> value == 0;
            case BIGGER -> value == 1;
            case NOT_EQUALS -> value != 0;
            case SMALLER -> value == -1;
            case BIGGER_OR_EQUALS -> value == 0 || value == 1;
            case SMALLER_OR_EQUALS -> value == 0 || value == -1;
        };
    }

    enum Operator {
        EQUALS,
        NOT_EQUALS,
        BIGGER,
        SMALLER,
        BIGGER_OR_EQUALS,
        SMALLER_OR_EQUALS

    }

}
