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

package com.wolfyscript.utilities.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver;
import com.wolfyscript.utilities.config.jackson.ValueDeserializer;
import com.wolfyscript.utilities.config.jackson.OptionalValueDeserializer;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = NBTTagConfig.OptionalValueDeserializer.class, delegateObjectDeserializer = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type", defaultImpl = NBTTagConfigCompound.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"type"})
public abstract class NBTTagConfig implements Keyed {

    private static final String ERROR_MISMATCH = "Mismatched NBT types! Requested type: %s but found type %s, at node %s.%s";

    @JsonIgnore
    protected final WolfyUtils wolfyUtils;
    @JsonIgnore
    protected final NamespacedKey type;
    @JsonIgnore
    protected NBTTagConfig parent;

    protected NBTTagConfig(@JacksonInject WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
    }

    protected NBTTagConfig(WolfyUtils wolfyUtils, NBTTagConfig parent) {
        this.wolfyUtils = wolfyUtils;
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        this.parent = parent;
    }

    public NamespacedKey getType() {
        return type;
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    @JsonIgnore
    protected void setParent(NBTTagConfig parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public NBTTagConfig getParent() {
        return parent;
    }

    public abstract NBTTagConfig copy();

    public static class OptionalValueDeserializer extends ValueDeserializer<NBTTagConfig> {

        private static final Pattern NUM_PATTERN = Pattern.compile("([0-9]+)([bBsSiIlL])|([0-9]?\\.?[0-9])+([fFdD])");

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
                    Matcher matcher = NUM_PATTERN.matcher(text);
                    if (matcher.matches()) {
                        String id = matcher.group(2);
                        if (id != null) {
                            // integer value
                        } else {
                            // float value
                            id = matcher.group(4);
                        }
                        yield switch (id.charAt(0)) {
                            case 'b', 'B' -> regNBTQueries.getKey(NBTTagConfigByte.class);
                            case 's', 'S' -> regNBTQueries.getKey(NBTTagConfigShort.class);
                            case 'i', 'I' -> regNBTQueries.getKey(NBTTagConfigInt.class);
                            case 'l', 'L' -> regNBTQueries.getKey(NBTTagConfigLong.class);
                            case 'f', 'F' -> regNBTQueries.getKey(NBTTagConfigFloat.class);
                            case 'd', 'D' -> regNBTQueries.getKey(NBTTagConfigDouble.class);
                            default -> regNBTQueries.getKey(NBTTagConfigString.class);
                        };
                    }
                    yield regNBTQueries.getKey(NBTTagConfigString.class);
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
