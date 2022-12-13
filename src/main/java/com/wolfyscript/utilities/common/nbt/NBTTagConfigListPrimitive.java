package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

public abstract class NBTTagConfigListPrimitive<VAL, T extends NBTTagConfigPrimitive<VAL>> extends NBTTagConfigList<T> {

    @JsonCreator
    NBTTagConfigListPrimitive(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<T>> elements, Class<T> elementType) {
        super(wolfyUtils, elements, elementType);
    }

    public NBTTagConfigListPrimitive(WolfyUtils wolfyUtils, NBTTagConfig parent, Class<T> elementType, List<Element<T>> elements) {
        super(wolfyUtils, parent, elementType, elements);
    }

    public NBTTagConfigListPrimitive(NBTTagConfigList<T> other) {
        super(other);
    }

}
