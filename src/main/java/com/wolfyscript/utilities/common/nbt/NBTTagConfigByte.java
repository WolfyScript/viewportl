package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
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
