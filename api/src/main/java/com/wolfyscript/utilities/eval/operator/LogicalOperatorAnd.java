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

package com.wolfyscript.utilities.eval.operator;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;

@KeyedStaticId(key = "and")
public class LogicalOperatorAnd extends LogicalOperator {

    @JsonProperty("that")
    protected final BoolOperator thatValue;

    @JsonCreator
    public LogicalOperatorAnd(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("this") BoolOperator thisValue, @JsonProperty("that") BoolOperator thatValue) {
        super(wolfyUtils, thisValue);
        this.thatValue = thatValue;
    }

    @Override
    public boolean evaluate(EvalContext context) {
        return thisValue.evaluate(context) && thatValue.evaluate(context);
    }
}
