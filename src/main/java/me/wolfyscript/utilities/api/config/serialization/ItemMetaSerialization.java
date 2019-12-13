package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;

public class ItemMetaSerialization implements JsonSerializer<ItemMeta>, JsonDeserializer<ItemMeta> {

    @Override
    public ItemMeta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(ItemMeta itemMeta, Type type, JsonSerializationContext jsonSerializationContext) {
        itemMeta.serialize();
        return null;
    }
}
