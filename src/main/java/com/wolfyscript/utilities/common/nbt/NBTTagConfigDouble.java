package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

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

}
