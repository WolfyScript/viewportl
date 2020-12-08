package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTypeSerialization {

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, PotionEffectType.class, (potionEffectType, gen, serializerProvider) -> {
            gen.writeString(potionEffectType.getName());
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            return PotionEffectType.getByName(node.asText());
        });
    }

}
