/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
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

package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.KeyedTypeResolver;
import com.wolfyscript.utilities.json.ValueDeserializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueDeserializer;
import java.io.IOException;
import java.util.Optional;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = NBTTagConfig.OptionalValueDeserializer.class, delegateObjectDeserializer = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type", defaultImpl = NBTTagConfigCompound.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"type"})
public abstract class NBTTagConfig implements Keyed {

    private static final String ERROR_MISMATCH = "Mismatched NBT types! Requested type: %s but found type %s, at node %s.%s";

    protected final WolfyUtils wolfyUtils;

    protected final NamespacedKey type;
    @JsonIgnore
    protected final String key;
    @JsonIgnore
    protected final NBTTagConfig parent;

    protected NBTTagConfig(@JacksonInject WolfyUtils wolfyUtils, @JacksonInject("nbt_tag_config.key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        this.wolfyUtils = wolfyUtils;
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        this.key = key;
        this.parent = parent;
    }

    @JsonGetter("type")
    public NamespacedKey getType() {
        return type;
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public abstract NBTTagConfig copy();

    public static class OptionalValueDeserializer extends ValueDeserializer<NBTTagConfig> {

        public OptionalValueDeserializer() {
            super(NBTTagConfig.class);
        }

        @Override
        public NBTTagConfig deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            if (jsonParser.isExpectedStartObjectToken()) {
                return null;
            }
            WolfyUtils wolfyUtils = (WolfyUtils) ctxt.findInjectableValue(WolfyUtils.class.getName(), null, null);
            var token = jsonParser.currentToken();
            JsonNode node = null;
            var regNBTQueries = wolfyUtils.getRegistries().getNbtTagConfigs();
            NamespacedKey type = switch (token) {
                case VALUE_STRING -> {
                    node = jsonParser.readValueAsTree();
                    var text = node.asText();
                    yield switch (!text.isBlank() ? text.charAt(text.length() - 1) : '0') {
                        case 'b', 'B' -> regNBTQueries.getKey(NBTTagConfigByte.class);
                        case 's', 'S' -> regNBTQueries.getKey(NBTTagConfigShort.class);
                        case 'i', 'I' -> regNBTQueries.getKey(NBTTagConfigInt.class);
                        case 'l', 'L' -> regNBTQueries.getKey(NBTTagConfigLong.class);
                        case 'f', 'F' -> regNBTQueries.getKey(NBTTagConfigFloat.class);
                        case 'd', 'D' -> regNBTQueries.getKey(NBTTagConfigDouble.class);
                        default -> regNBTQueries.getKey(NBTTagConfigString.class);
                    };
                }
                case VALUE_NUMBER_INT -> regNBTQueries.getKey(NBTTagConfigInt.class);
                case VALUE_NUMBER_FLOAT -> regNBTQueries.getKey(NBTTagConfigDouble.class);
                case VALUE_FALSE, VALUE_TRUE -> regNBTQueries.getKey(NBTTagConfigBoolean.class);
                default -> null;
            };
            if (type == null) return null;
            if (node == null) {
                node = jsonParser.readValueAsTree();
            }
            ObjectNode objNode = new ObjectNode(ctxt.getNodeFactory());
            objNode.put("type", type.toString());
            objNode.set("value", node);
            return ctxt.readTreeAsValue(objNode, NBTTagConfig.class);
        }
    }

}
