package me.wolfyscript.utilities.api.utils.json.gson.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.*;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

public class CustomItemSerialization implements JsonSerializer<CustomItem>, JsonDeserializer<CustomItem> {

    @Override
    public CustomItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
        AtomicReference<CustomItem> customItem = new AtomicReference<>();
        if (jsonElement instanceof JsonObject) {
            JsonObject object = jsonElement.getAsJsonObject();

            object.entrySet().stream().filter(entry -> !entry.getKey().equals("custom_amount")).findFirst().ifPresent(entry -> {
                String key = entry.getKey();
                JsonElement element = entry.getValue();

                switch (key){
                    case "item":
                        customItem.set(new CustomItem((VanillaRef) deserializationContext.deserialize(element, VanillaRef.class)));
                        break;
                    case "wolfyutilities":
                    case "item_key":
                        customItem.set(new CustomItem((WolfyUtilitiesRef) deserializationContext.deserialize(element, WolfyUtilitiesRef.class)));
                        break;
                    case "oraxen":
                        customItem.set(new CustomItem((OraxenRef) deserializationContext.deserialize(element, OraxenRef.class)));
                        break;
                    case "itemsadder":
                        customItem.set(new CustomItem((ItemsAdderRef) deserializationContext.deserialize(element, ItemsAdderRef.class)));
                        break;
                    case "mythicmobs":
                        customItem.set(new CustomItem((MythicMobsRef) deserializationContext.deserialize(element, MythicMobsRef.class)));
                        break;
                    case "mmoitems":
                        customItem.set(new CustomItem((MMOItemsRef) deserializationContext.deserialize(element, MMOItemsRef.class)));

                }
            });
            if(object.has("custom_amount")){
                if(customItem.get() != null){
                    customItem.get().setAmount(object.get("custom_amount").getAsInt());
                }
            }
        }
        return customItem.get() == null ? new CustomItem(Material.AIR) : customItem.get();
    }

    @Override
    public JsonElement serialize(CustomItem customItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("custom_amount", customItem.getAmount() != customItem.getApiReference().getLinkedItem().getAmount() ? customItem.getAmount() : 0);
        //return customItem.getApiReference().serialize(jsonObject, jsonSerializationContext);
        return jsonObject;
    }
}