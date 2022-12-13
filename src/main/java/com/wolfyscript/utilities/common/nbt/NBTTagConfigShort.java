package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

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

}
