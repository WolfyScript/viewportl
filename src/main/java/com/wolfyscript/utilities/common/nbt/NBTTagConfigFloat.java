package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderFloatConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigFloat.OptionalValueSerializer.class)
@KeyedStaticId(key = "float")
public class NBTTagConfigFloat extends NBTTagConfigPrimitive<Float> {

    @JsonCreator
    NBTTagConfigFloat(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Float> valueNode) {
        super(wolfyUtils, valueNode);
    }

    public NBTTagConfigFloat(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<Float> value) {
        super(wolfyUtils, parent, value);
    }

    private NBTTagConfigFloat(NBTTagConfigFloat other) {
        super(other);
    }

    @Override
    public NBTTagConfigFloat copy() {
        return new NBTTagConfigFloat(this);
    }

    public static class OptionalValueSerializer extends ValueSerializer<NBTTagConfigFloat> {

        public OptionalValueSerializer() {
            super(NBTTagConfigFloat.class);
        }

        @Override
        public boolean serialize(NBTTagConfigFloat targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderFloatConst floatConst) {
                generator.writeObject(floatConst);
                return true;
            }
            return false;
        }
    }
}
