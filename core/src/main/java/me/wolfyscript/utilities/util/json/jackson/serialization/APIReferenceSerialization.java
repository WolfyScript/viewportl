package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
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
                    APIReference.Parser<?> parser = CustomItem.getApiReferenceParser(key);
                    if (parser != null) {
                        JsonNode element = entry.getValue();
                        if (element != null) {
                            apiReference = parser.parse(element);
                            break;
                        }
                    }
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
