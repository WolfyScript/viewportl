package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.main.WUPlugin;
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
            WUPlugin.getWolfyUtilities().sendConsoleWarning("Error Deserializing Color! Invalid Color object!");
            return null;
        });
    }
}
