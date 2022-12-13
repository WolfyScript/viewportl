package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

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

}
