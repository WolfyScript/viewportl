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

@KeyedStaticId(key = "byte_array")
public class QueryNodeByteArray extends QueryNodePrimitive<byte[]> {

    @JsonCreator
    public QueryNodeByteArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<byte[]> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByteArray;
    }

    private QueryNodeByteArray(QueryNodeByteArray other) {
        super(other.wolfyUtils, other.value, other.key, other.parentPath);
    }

    @Override
    protected Optional<byte[]> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getByteArray(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, byte[] value, NBTCompound resultContainer) {
        resultContainer.setByteArray(key, value);
    }

    @Override
    public QueryNodeByteArray copy() {
        return new QueryNodeByteArray(this);
    }

}
