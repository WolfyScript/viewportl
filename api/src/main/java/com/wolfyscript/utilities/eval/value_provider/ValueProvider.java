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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver;
import com.wolfyscript.utilities.config.jackson.OptionalValueDeserializer;
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = ValueProvider.ValueDeserializer.class)
@OptionalValueSerializer(serializer = ValueProvider.ValueSerializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key"})
public interface ValueProvider<V> extends Keyed {

    @JsonIgnore
    V getValue(EvalContext context);

    @JsonIgnore
    default V getValue() {
        return getValue(new EvalContext());
    }

    @JsonGetter("key")
    @Override
    NamespacedKey getNamespacedKey();

    class ValueDeserializer extends com.wolfyscript.utilities.config.jackson.ValueDeserializer<ValueProvider<?>> {

        private static final Pattern NUM_PATTERN = Pattern.compile("([0-9]+)([bBsSiIlL])|([0-9]?\\.?[0-9])+([fFdD])");

        public ValueDeserializer() {
            super((Class<ValueProvider<?>>)(Object) ValueProvider.class);
        }

        @Override
        public ValueProvider<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            WolfyUtils wolfyUtils = (WolfyUtils) ctxt.findInjectableValue(WolfyUtils.class.getName(), null, null);
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                JsonNode node = p.readValueAsTree();
                String text = node.asText();
                if (!text.isBlank()) {
                    Matcher matcher = NUM_PATTERN.matcher(text);
                    if (matcher.matches()) {
                        String value;
                        String id = matcher.group(2);
                        if (id != null) {
                            // integer value
                            value = matcher.group(1);
                        } else {
                            // float value
                            id = matcher.group(4);
                            value = matcher.group(3);
                        }
                        try {
                            return switch (id.charAt(0)) {
                                case 's', 'S' -> new ValueProviderShortConst(wolfyUtils, Short.parseShort(value));
                                case 'i', 'I' -> new ValueProviderIntegerConst(wolfyUtils, Integer.parseInt(value));
                                case 'l', 'L' -> new ValueProviderLongConst(wolfyUtils, Long.parseLong(value));
                                case 'f', 'F' -> new ValueProviderFloatConst(wolfyUtils, Float.parseFloat(value));
                                case 'd', 'D' -> new ValueProviderDoubleConst(wolfyUtils, Double.parseDouble(value));
                                default -> new ValueProviderStringConst(wolfyUtils, text);
                            };
                        } catch (NumberFormatException e) {
                            // Cannot parse the value. Might a String value!
                        }
                    }
                    return new ValueProviderStringConst(wolfyUtils, text);
                }
            } else if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                return new ValueProviderIntegerConst(wolfyUtils, ctxt.readValue(p, Integer.class));
            } else if (p.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
                return new ValueProviderDoubleConst(wolfyUtils, ctxt.readValue(p, Double.class));
            }
            return null;
        }
    }

    class ValueSerializer extends com.wolfyscript.utilities.config.jackson.ValueSerializer<ValueProvider<?>> {

        public ValueSerializer() {
            super((Class<ValueProvider<?>>)(Object) ValueProvider.class);
        }

        @Override
        public boolean serialize(ValueProvider<?> valueProvider, JsonGenerator generator, SerializerProvider provider) throws IOException {
            System.out.println("Serialize ValueProvider!");
            if (valueProvider instanceof ValueProviderStringConst stringConst) {
                generator.writeString(stringConst.getValue());
                return true;
            } else if (valueProvider instanceof ValueProviderByteConst byteConst) {
                generator.writeString(byteConst.getValue() + "b");
                return true;
            } else if (valueProvider instanceof ValueProviderShortConst shortConst) {
                generator.writeString(shortConst.getValue().byteValue() + "s");
                return true;
            } else if (valueProvider instanceof ValueProviderIntegerConst integerConst) {
                generator.writeNumber(integerConst.getValue());
                return true;
            } else if (valueProvider instanceof ValueProviderLongConst longConst) {
                generator.writeString(longConst.getValue() + "L");
                return true;
            } else if (valueProvider instanceof ValueProviderFloatConst floatConst) {
                generator.writeString(floatConst.getValue() + "f");
                return true;
            } else if (valueProvider instanceof ValueProviderDoubleConst doubleConst) {
                generator.writeString(doubleConst.getValue() + "d");
                return true;
            }
            return false;
        }
    }
}
