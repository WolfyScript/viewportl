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
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDoubleConst;
import com.wolfyscript.utilities.config.jackson.ValueSerializer;
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigDouble.OptionalValueSerializer.class)
@KeyedStaticId(key = "double")
public class NBTTagConfigDouble extends NBTTagConfigPrimitive<Double> {

    @JsonCreator
    NBTTagConfigDouble(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Double> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigDouble(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<Double> value) {
        super(wolfyUtils, parent, value);
    }

    private NBTTagConfigDouble(NBTTagConfigDouble other) {
        super(other);
    }

    @Override
    public NBTTagConfigDouble copy() {
        return new NBTTagConfigDouble(this);
    }

    public static class OptionalValueSerializer extends ValueSerializer<NBTTagConfigDouble> {

        public OptionalValueSerializer() {
            super(NBTTagConfigDouble.class);
        }

        @Override
        public boolean serialize(NBTTagConfigDouble targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderDoubleConst doubleConst) {
                generator.writeObject(doubleConst);
                return true;
            }
            return false;
        }
    }
}
