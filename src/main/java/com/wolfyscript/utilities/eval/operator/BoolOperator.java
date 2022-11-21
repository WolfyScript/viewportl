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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.json.ValueDeserializer;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import com.wolfyscript.utilities.json.annotations.OptionalValueDeserializer;
import java.io.IOException;

/**
 * An Operator that evaluates into a booleanish value.
 */
@KeyedBaseType(baseType = Operator.class)
@OptionalValueDeserializer(deserializer = BoolOperator.OptionalValueDeserializer.class)
public abstract class BoolOperator extends Operator {

    public BoolOperator(NamespacedKey namespacedKey) {
        super(namespacedKey);
    }

    public BoolOperator(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    public abstract boolean evaluate(EvalContext context);

    public static class OptionalValueDeserializer extends ValueDeserializer<BoolOperator> {

        private final WolfyUtils wolfyUtils;

        public OptionalValueDeserializer(WolfyUtils wolfyUtils) {
            super(BoolOperator.class);
            this.wolfyUtils = wolfyUtils;
        }

        @Override
        public BoolOperator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.readValueAsTree();
            return new BoolOperatorConst(wolfyUtils, node.asBoolean());
        }
    }
}
