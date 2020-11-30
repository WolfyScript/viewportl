package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Color;
import org.bukkit.Particle;

public class DustOptionsSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, Particle.DustOptions.class, (dustOptions, gen, s) -> {
            gen.writeStartObject();
            gen.writeNumberField("size", dustOptions.getSize());
            gen.writeObjectField("color", dustOptions.getColor());
            gen.writeEndObject();
        }, (p, ctxt) -> {
            p.setCodec(JacksonUtil.getObjectMapper());
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                float size = node.get("size").floatValue();
                Color color = ctxt.readValue(node.get("color").traverse(JacksonUtil.getObjectMapper()), Color.class);
                return new Particle.DustOptions(color, size);
            }
            WUPlugin.getWolfyUtilities().sendConsoleWarning("Error Deserializing DustOptions! Invalid DustOptions object!");
            return null;
        });
    }
}
