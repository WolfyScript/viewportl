package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.custom_items.api_references.*;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

public class APIReferenceSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, APIReference.class, (value, gen, provider) -> {
            gen.writeStartObject();
            gen.writeNumberField("custom_amount", value.getAmount());
            value.serialize(gen, provider);
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            APIReference apiReference = null;
            int customAmount = 0;
            if (node.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = iterator.next();
                    if (entry.getKey().equals("custom_amount")) {
                        customAmount = entry.getValue().asInt();
                        continue;
                    }
                    String key = entry.getKey();
                    JsonNode element = entry.getValue();
                    switch (key){
                        case "item":
                            apiReference = JacksonUtil.getObjectMapper().treeToValue(element, VanillaRef.class);
                            break;
                        case "wolfyutilities":
                        case "item_key":
                            apiReference = JacksonUtil.getObjectMapper().treeToValue(element, WolfyUtilitiesRef.class);
                            break;
                        case "oraxen":
                            apiReference = JacksonUtil.getObjectMapper().treeToValue(element, OraxenRef.class);
                            break;
                        case "itemsadder":
                            apiReference = JacksonUtil.getObjectMapper().treeToValue(element, ItemsAdderRef.class);
                            break;
                        case "mythicmobs":
                            apiReference = JacksonUtil.getObjectMapper().treeToValue(element, MythicMobsRef.class);
                            break;
                        case "mmoitems":
                            apiReference = JacksonUtil.getObjectMapper().treeToValue(element, MMOItemsRef.class);
                    }
                    //Break if CustomItem was found, else check next fields
                    if(apiReference != null) break;
                }

            } else if (node.isTextual()) {
                //Legacy items saved as string!
                apiReference = JacksonUtil.getObjectMapper().treeToValue(node, VanillaRef.class);
            }
            if (apiReference != null) {
                apiReference.setAmount(customAmount);
                return apiReference;
            }
            return new VanillaRef(new ItemStack(Material.AIR));
        });
    }
}
