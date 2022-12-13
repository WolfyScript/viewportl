package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/int_array")
public class NBTTagConfigListIntArray extends NBTTagConfigListPrimitive<int[], NBTTagConfigIntArray> {

    @JsonCreator
    NBTTagConfigListIntArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigIntArray>> elements) {
        super(wolfyUtils, elements, NBTTagConfigIntArray.class);
    }

    public NBTTagConfigListIntArray(WolfyUtils wolfyUtils, NBTTagConfig parent, List<Element<NBTTagConfigIntArray>> elements) {
        super(wolfyUtils, parent, NBTTagConfigIntArray.class, elements);
    }

    public NBTTagConfigListIntArray(NBTTagConfigList<NBTTagConfigIntArray> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListIntArray copy() {
        return new NBTTagConfigListIntArray(this);
    }
}
