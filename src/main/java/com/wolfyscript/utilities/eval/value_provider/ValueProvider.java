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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.KeyedTypeResolver;
import com.wolfyscript.utilities.json.annotations.OptionalValueDeserializer;
import java.io.IOException;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = ValueProvider.ValueDeserializer.class)
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

    class ValueDeserializer extends com.wolfyscript.utilities.json.ValueDeserializer<ValueProvider<?>> {

        private final WolfyUtils wolfyUtils;

        public ValueDeserializer(WolfyUtils wolfyUtils) {
            super((Class<ValueProvider<?>>)(Object) ValueProvider.class);
            this.wolfyUtils = wolfyUtils;
        }

        @Override
        public ValueProvider<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.readValueAsTree();
            if (node.isTextual()) {
                String text = node.asText();
                if (!text.isBlank()) {
                    char identifier = text.charAt(text.length() - 1);
                    String value = text.substring(0, text.length() - 1);
                    try {
                        return switch (identifier) {
                            case 's', 'S' -> new ValueProviderShortConst(wolfyUtils, Short.parseShort(value));
                            case 'i', 'I' -> new ValueProviderIntegerConst(wolfyUtils, Integer.parseInt(value));
                            case 'l', 'L' -> new ValueProviderLongConst(wolfyUtils, Long.parseLong(value));
                            case 'f', 'F' -> new ValueProviderFloatConst(wolfyUtils, Float.parseFloat(value));
                            case 'd', 'D' -> new ValueProviderDoubleConst(wolfyUtils, Double.parseDouble(value));
                            default -> new ValueProviderStringConst(wolfyUtils, text);
                        };
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    return new ValueProviderStringConst(wolfyUtils, text);
                }
            }
            return null;
        }
    }
}
