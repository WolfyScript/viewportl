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

package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.config.jackson.OptionalValueDeserializer;
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer;

import java.io.IOException;

@OptionalValueDeserializer(deserializer = Position.ValueDeserializer.class)
@OptionalValueSerializer(serializer = Position.ValueSerializer.class)
public class Position {

    private final Type type;
    private final int slot;

    @JsonCreator
    public Position(@JsonProperty("type") Type type, @JsonProperty("slot") int slot) {
        this.type = type;
        this.slot = slot;
    }

    public Type type() {
        return type;
    }

    public int slot() {
        return slot;
    }

    public static Position relative(int slot) {
        return new Position(Type.RELATIVE, slot);
    }

    public static Position absolute(int slot) {
        return new Position(Type.ABSOLUTE, slot);
    }

    public enum Type {
        /**
         * Relative position with the parent component as the origin
         */
        @JsonAlias("relative")
        RELATIVE,
        /**
         * Absolute position with the Window as the origin
         */
        @JsonAlias("absolute")
        ABSOLUTE
    }

    public static class ValueDeserializer extends com.wolfyscript.utilities.config.jackson.ValueDeserializer<Position> {

        public ValueDeserializer() {
            super(Position.class);
        }

        @Override
        public Position deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                return Position.absolute(p.getIntValue());
            }
            return Position.absolute(0);
        }
    }

    public static class ValueSerializer extends com.wolfyscript.utilities.config.jackson.ValueSerializer<Position> {

        public ValueSerializer() {
            super(Position.class);
        }

        @Override
        public boolean serialize(Position targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.type() == Type.ABSOLUTE) {
                generator.writeNumber(targetObject.slot());
                return true;
            }
            return false;
        }
    }

}
