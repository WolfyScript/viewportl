package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/compound")
public class NBTTagConfigListCompound extends NBTTagConfigList<NBTTagConfigCompound> {

    @JsonCreator
    NBTTagConfigListCompound(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("values") List<NBTTagConfigCompound> elements) {
        super(wolfyUtils, elements, NBTTagConfigCompound.class);
    }

    public NBTTagConfigListCompound(WolfyUtils wolfyUtils, NBTTagConfig parent, List<NBTTagConfigCompound> elements) {
        super(wolfyUtils, parent, NBTTagConfigCompound.class, elements);
    }

    private NBTTagConfigListCompound(NBTTagConfigListCompound other) {
        super(other);
    }

    @Override
    public NBTTagConfigListCompound copy() {
        return new NBTTagConfigListCompound(this);
    }
}
