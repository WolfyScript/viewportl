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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteConst;
import com.wolfyscript.utilities.config.jackson.ValueSerializer;
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigByte.OptionalValueSerializer.class)
@KeyedStaticId(key = "byte")
public class NBTTagConfigByte extends NBTTagConfigPrimitive<Byte> {

    @JsonCreator
    NBTTagConfigByte(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Byte> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigByte(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<Byte> value) {
        super(wolfyUtils, parent, value);
    }

    private NBTTagConfigByte(NBTTagConfigByte other) {
        super(other);
    }

    @Override
    public NBTTagConfigByte copy() {
        return new NBTTagConfigByte(this);
    }

    public static class OptionalValueSerializer extends ValueSerializer<NBTTagConfigByte> {

        public OptionalValueSerializer() {
            super(NBTTagConfigByte.class);
        }

        @Override
        public boolean serialize(NBTTagConfigByte targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderByteConst byteConst) {
                generator.writeObject(byteConst);
                return true;
            }
            return false;
        }
    }
}
