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

@KeyedStaticId(key = "string")
public class QueryNodeString extends QueryNodePrimitive<String> {

    @JsonCreator
    public QueryNodeString(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<String> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagString;
    }

    public QueryNodeString(QueryNodePrimitive<String> other) {
        super(other);
    }

    @Override
    protected Optional<String> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getString(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, String value, NBTCompound resultContainer) {
        resultContainer.setString(key, value);
    }

    @Override
    public QueryNodeString copy() {
        return new QueryNodeString(this);
    }

}
