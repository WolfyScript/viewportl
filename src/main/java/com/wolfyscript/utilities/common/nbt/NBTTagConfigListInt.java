package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/int")
public class NBTTagConfigListInt extends NBTTagConfigListPrimitive<Integer, NBTTagConfigInt> {

    @JsonCreator
    NBTTagConfigListInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("values") List<NBTTagConfigInt> elements) {
        super(wolfyUtils, elements, NBTTagConfigInt.class);
    }

    public NBTTagConfigListInt(WolfyUtils wolfyUtils, NBTTagConfig parent,  List<NBTTagConfigInt> elements) {
        super(wolfyUtils, parent, NBTTagConfigInt.class, elements);
    }

    public NBTTagConfigListInt(NBTTagConfigList<NBTTagConfigInt> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListInt copy() {
        return new NBTTagConfigListInt(this);
    }
}
