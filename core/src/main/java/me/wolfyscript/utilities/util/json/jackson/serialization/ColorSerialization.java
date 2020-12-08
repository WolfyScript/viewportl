package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Color;

public class ColorSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, Color.class, (value, gen, serializerProvider) -> {
            gen.writeStartObject();
            gen.writeNumberField("red", value.getRed());
            gen.writeNumberField("green", value.getGreen());
            gen.writeNumberField("blue", value.getBlue());
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                int red = node.get("red").asInt();
                int green = node.get("green").asInt();
                int blue = node.get("blue").asInt();
                return Color.fromBGR(blue, green, red);
            }
            WolfyUtilities.getWUCore().getChat().sendConsoleWarning("Error Deserializing Color! Invalid Color object!");
            return null;
        });
    }
}
