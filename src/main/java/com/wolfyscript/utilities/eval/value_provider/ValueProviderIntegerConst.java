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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = ValueProviderIntegerConst.ValueSerializer.class)
@KeyedStaticId(key = "int/const")
public class ValueProviderIntegerConst extends AbstractValueProvider<Integer> implements ValueProviderInteger {

    private final int value;

    @JsonCreator
    public ValueProviderIntegerConst(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") int value) {
        super(wolfyUtils);
        this.value = value;
    }

    @Override
    public Integer getValue(EvalContext context) {
        return value;
    }

    public static class ValueSerializer extends com.wolfyscript.utilities.json.ValueSerializer<ValueProviderIntegerConst> {

        public ValueSerializer() {
            super(ValueProviderIntegerConst.class);
        }

        @Override
        public boolean serialize(ValueProviderIntegerConst valueProvider, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeNumber(valueProvider.getValue());
            return true;
        }
    }
}
