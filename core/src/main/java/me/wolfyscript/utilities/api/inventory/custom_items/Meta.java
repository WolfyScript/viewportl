package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Meta {

    protected MetaSettings.Option option;

    private final String id;

    private List<MetaSettings.Option> availableOptions;

    protected Meta(String id) {
        this.id = id;
    }

    public MetaSettings.Option getOption() {
        return option;
    }

    public void setOption(MetaSettings.Option option) {
        this.option = option;
    }

    public boolean isExact() {
        return option.equals(MetaSettings.Option.EXACT);
    }

    public List<MetaSettings.Option> getAvailableOptions() {
        return availableOptions;
    }

    protected void setAvailableOptions(MetaSettings.Option... options) {
        if (options != null) {
            availableOptions = Arrays.asList(options);
        }
    }

    public abstract boolean check(ItemBuilder itemOther, ItemBuilder item);

    public void writeToJson(JsonGenerator gen) throws IOException {
        gen.writeString(option.toString());
    }

    public void readFromJson(@NotNull JsonNode node){
        this.option = MetaSettings.Option.valueOf(node.asText());
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meta)) return false;
        Meta meta = (Meta) o;
        return option == meta.option &&
                Objects.equals(id, meta.id) &&
                Objects.equals(availableOptions, meta.availableOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, id, availableOptions);
    }

    @Deprecated
    public String toString() {
        return option.toString();
    }

    @Deprecated
    public void parseFromJSON(String value) {
        this.option = MetaSettings.Option.valueOf(value);
    }
}
