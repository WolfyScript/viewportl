package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/double")
public class NBTTagConfigListDouble extends NBTTagConfigListPrimitive<Double, NBTTagConfigDouble> {

    @JsonCreator
    NBTTagConfigListDouble(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigDouble>> elements) {
        super(wolfyUtils, elements, NBTTagConfigDouble.class);
    }

    public NBTTagConfigListDouble(WolfyUtils wolfyUtils, NBTTagConfig parent, List<Element<NBTTagConfigDouble>> elements) {
        super(wolfyUtils, parent, NBTTagConfigDouble.class, elements);
    }

    public NBTTagConfigListDouble(NBTTagConfigList<NBTTagConfigDouble> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListDouble copy() {
        return new NBTTagConfigListDouble(this);
    }
}
