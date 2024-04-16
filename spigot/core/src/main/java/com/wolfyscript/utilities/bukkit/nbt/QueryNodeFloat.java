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

@KeyedStaticId(key = "float")
public class QueryNodeFloat extends QueryNodePrimitive<Float> {

    @JsonCreator
    public QueryNodeFloat(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Float> valueNode, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, valueNode, key, parentPath);
        this.nbtType = NBTType.NBTTagFloat;
    }

    private QueryNodeFloat(QueryNodeFloat other) {
        super(other);
    }

    @Override
    protected Optional<Float> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getFloat(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Float value, NBTCompound resultContainer) {
        resultContainer.setFloat(key, value);
    }

    @Override
    public QueryNodeFloat copy() {
        return new QueryNodeFloat(this);
    }

}
