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
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueDeserializer;

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

    class ValueDeserializer extends me.wolfyscript.utilities.util.json.jackson.ValueDeserializer<ValueProvider<?>> {

        public ValueDeserializer() {
            super((Class<ValueProvider<?>>)(Object) ValueProvider.class);
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
                            case 'i', 'I' -> new ValueProviderIntegerConst(Integer.parseInt(value));
                            case 'f', 'F' -> new ValueProviderFloatConst(Float.parseFloat(value));
                            default -> new ValueProviderStringConst(text);
                        };
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    return new ValueProviderStringConst(text);
                }
            }
            return null;
        }
    }
}
