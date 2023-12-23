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
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = ValueProviderStringConst.ValueSerializer.class)
@KeyedStaticId(key = "string/const")
public class ValueProviderStringConst extends AbstractValueProvider<String> {

    private final String value;

    @JsonCreator
    public ValueProviderStringConst(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") String value) {
        super(wolfyUtils);
        this.value = value;
    }

    @Override
    public String getValue(EvalContext context) {
        return value;
    }

    public static class ValueSerializer extends com.wolfyscript.utilities.config.jackson.ValueSerializer<ValueProviderStringConst> {

        public ValueSerializer() {
            super(ValueProviderStringConst.class);
        }

        @Override
        public boolean serialize(ValueProviderStringConst valueProvider, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(valueProvider.getValue());
            return true;
        }
    }
}
