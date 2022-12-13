package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

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

}
