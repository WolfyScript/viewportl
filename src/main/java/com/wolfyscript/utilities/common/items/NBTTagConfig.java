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

package com.wolfyscript.utilities.common.items;

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
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.KeyedTypeResolver;
import com.wolfyscript.utilities.json.ValueDeserializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueDeserializer;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@OptionalValueDeserializer(deserializer = NBTTagConfig.NBTTagValueDeserializer.class)
@JsonTypeInfo(defaultImpl = NBTTagConfigCompound.class, use = JsonTypeInfo.Id.CUSTOM, property = "type")
@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonPropertyOrder(value = {"type"})
public class NBTTagConfig implements Keyed {

    private final NamespacedKey type;

    public NBTTagConfig(WolfyUtils wolfyUtils) {
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    public static class NBTTagValueDeserializer extends ValueDeserializer<NBTTagConfig> {

        protected NBTTagValueDeserializer() {
            super(NBTTagConfig.class);
        }

        @Override
        public NBTTagConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            WolfyUtils wolfyUtils = (WolfyUtils) ctxt.findInjectableValue(WolfyUtils.class.getName(), null, null);
            if (p.isExpectedStartArrayToken()) {
                List<JsonNode> nodes = ctxt.readValue(p, (Class<List<JsonNode>>) (Object) List.class);
                if (nodes.size() == 2 && isValueIndicator(nodes.get(0)) && nodes.get(1).isObject()) {
                    // Object with value indicator
                    ValueProvider<?> valueProvider = ctxt.readValue(p, ValueProvider.class);
                    // TODO: Save the value Provider
                    return null;
                }
                // Read Config List
                return new NBTTagConfigList(wolfyUtils, nodes.stream().map(node -> {
                    try {
                        return ctxt.readTreeAsValue(node, NBTTagConfig.class);
                    } catch (IOException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).toList());
            }
            // Read Default (Compound) or perhaps other specified type.
            return null;
        }

        private static boolean isValueIndicator(JsonNode node) {
            if (!node.isTextual()) return false;
            String value = node.asText();
            return value.equalsIgnoreCase("#") || value.equalsIgnoreCase("val");
        }
    }

}
