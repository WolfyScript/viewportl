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
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigInt.OptionalValueSerializer.class)
@KeyedStaticId(key = "int")
public class NBTTagConfigInt extends NBTTagConfigPrimitive<Integer> {

    @JsonCreator
    NBTTagConfigInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Integer> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigInt(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<Integer> value) {
        super(wolfyUtils, parent, value);
    }

    public NBTTagConfigInt(NBTTagConfigPrimitive<Integer> other) {
        super(other);
    }

    @Override
    public NBTTagConfigPrimitive<Integer> copy() {
        return new NBTTagConfigInt(this);
    }

    public static class OptionalValueSerializer extends ValueSerializer<NBTTagConfigInt> {

        public OptionalValueSerializer() {
            super(NBTTagConfigInt.class);
        }

        @Override
        public boolean serialize(NBTTagConfigInt targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderIntegerConst integerConst) {
                generator.writeObject(integerConst);
                return true;
            }
            return false;
        }
    }
}
