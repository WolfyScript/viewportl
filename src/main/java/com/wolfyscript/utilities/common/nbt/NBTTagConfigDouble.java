package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDoubleConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
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
