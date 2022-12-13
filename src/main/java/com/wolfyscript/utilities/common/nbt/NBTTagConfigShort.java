package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShortConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigShort.OptionalValueProvider.class)
@KeyedStaticId(key = "short")
public class NBTTagConfigShort extends NBTTagConfigPrimitive<Short> {

    @JsonCreator
    NBTTagConfigShort(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Short> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigShort(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<Short> value) {
        super(wolfyUtils, parent, value);
    }

    public NBTTagConfigShort(NBTTagConfigPrimitive<Short> other) {
        super(other);
    }

    @Override
    public NBTTagConfigShort copy() {
        return new NBTTagConfigShort(this);
    }

    public static class OptionalValueProvider extends ValueSerializer<NBTTagConfigShort> {

        public OptionalValueProvider() {
            super(NBTTagConfigShort.class);
        }

        @Override
        public boolean serialize(NBTTagConfigShort targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderShortConst shortConst) {
                generator.writeObject(shortConst);
                return true;
            }
            return false;
        }
    }
}
