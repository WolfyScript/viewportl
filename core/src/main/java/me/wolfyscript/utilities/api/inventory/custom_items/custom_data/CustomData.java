package me.wolfyscript.utilities.api.inventory.custom_items.custom_data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Objects;

public abstract class CustomData {

    private final String id;

    protected CustomData(String id){
        this.id = id;
    }

    public abstract CustomData getDefaultCopy();

    public abstract void writeToJson(JsonGenerator gen) throws IOException;

    public abstract CustomData readFromJson(JsonNode node) throws IOException;

    public  String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomData that = (CustomData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
