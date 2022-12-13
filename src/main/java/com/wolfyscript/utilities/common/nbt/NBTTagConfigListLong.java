package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/long")
public class NBTTagConfigListLong extends NBTTagConfigListPrimitive<Long, NBTTagConfigLong> {

    @JsonCreator
    NBTTagConfigListLong(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigLong>> elements) {
        super(wolfyUtils, elements, NBTTagConfigLong.class);
    }

    public NBTTagConfigListLong(WolfyUtils wolfyUtils, NBTTagConfig parent, List<Element<NBTTagConfigLong>> elements) {
        super(wolfyUtils, parent, NBTTagConfigLong.class, elements);
    }

    public NBTTagConfigListLong(NBTTagConfigList<NBTTagConfigLong> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListLong copy() {
        return new NBTTagConfigListLong(this);
    }
}
