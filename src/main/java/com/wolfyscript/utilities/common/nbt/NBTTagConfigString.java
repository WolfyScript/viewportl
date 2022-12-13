package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigString.OptionalValueSerializer.class)
@KeyedStaticId(key = "string")
public class NBTTagConfigString extends NBTTagConfigPrimitive<String> {

    @JsonCreator
    NBTTagConfigString(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<String> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigString(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<String> value) {
        super(wolfyUtils, parent, value);
    }

    public NBTTagConfigString(NBTTagConfigPrimitive<String> other) {
        super(other);
    }

    @Override
    public NBTTagConfigString copy() {
        return new NBTTagConfigString(this);
    }

    public static class OptionalValueSerializer extends ValueSerializer<NBTTagConfigString> {

        public OptionalValueSerializer() {
            super(NBTTagConfigString.class);
        }

        @Override
        public boolean serialize(NBTTagConfigString targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderStringConst stringConst) {
                generator.writeObject(stringConst);
                return true;
            }
            return false;
        }
    }
}
