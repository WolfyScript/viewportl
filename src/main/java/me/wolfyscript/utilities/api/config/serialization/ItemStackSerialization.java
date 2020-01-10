package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackSerialization implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement instanceof JsonPrimitive){
            String value = jsonElement.getAsString();
            if (!value.startsWith("{")) {
                return ItemUtils.deserializeItemStack(jsonElement.getAsString());
            }
            if (value == null || value.equals("empty")) {
                return null;
            }
            return ItemUtils.convertJsontoItemStack(value);
        }
        return null;
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
        if(itemStack != null){
            return new JsonPrimitive(ItemUtils.convertItemStackToJson(itemStack));
        }
        return null;
    }
}
