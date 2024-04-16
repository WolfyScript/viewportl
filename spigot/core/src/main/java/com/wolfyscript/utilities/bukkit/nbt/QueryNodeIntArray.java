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

@KeyedStaticId(key = "int_array")
public class QueryNodeIntArray extends QueryNodePrimitive<int[]> {

    @JsonCreator
    public QueryNodeIntArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<int[]> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByteArray;
    }

    public QueryNodeIntArray(QueryNodeIntArray other) {
        super(other.wolfyUtils, other.value, other.key, other.parentPath);
    }

    @Override
    protected Optional<int[]> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getIntArray(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, int[] value, NBTCompound resultContainer) {
        resultContainer.setIntArray(key, value);
    }

    @Override
    public QueryNodeIntArray copy() {
        return new QueryNodeIntArray(this);
    }

}
