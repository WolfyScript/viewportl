package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

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

}
