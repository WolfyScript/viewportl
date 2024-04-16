package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Optional;

@KeyedStaticId(key = "byte")
public class QueryNodeByte extends QueryNodePrimitive<Byte> {

    @JsonCreator
    public QueryNodeByte(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Byte> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByte;
    }

    private QueryNodeByte(QueryNodeByte other) {
        super(other);
    }

    @Override
    protected Optional<Byte> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getByte(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Byte value, NBTCompound resultContainer) {
        resultContainer.setByte(key, value);
    }

    @Override
    public QueryNodeByte copy() {
        return new QueryNodeByte(this);
    }

}
