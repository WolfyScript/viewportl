package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "int_array")
public class NBTTagConfigIntArray extends NBTTagConfigPrimitive<int[]> {

    @JsonCreator
    NBTTagConfigIntArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<int[]> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigIntArray(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<int[]> value) {
        super(wolfyUtils, parent, value);
    }

    public NBTTagConfigIntArray(NBTTagConfigIntArray other) {
        super(other.wolfyUtils, other.value);
    }

    @Override
    public NBTTagConfigIntArray copy() {
        return new NBTTagConfigIntArray(this);
    }

}
