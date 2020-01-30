package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.lang.reflect.Type;

public class DustOptionsSerialization implements JsonSerializer<Particle.DustOptions>, JsonDeserializer<Particle.DustOptions> {

    @Override
    public Particle.DustOptions deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            float size = ((JsonObject) jsonElement).get("size").getAsFloat();
            Color color = jsonDeserializationContext.deserialize(((JsonObject) jsonElement).get("color"), Color.class);
            return new Particle.DustOptions(color, size);
        } else {
            Main.getMainUtil().sendConsoleWarning("Error Deserializing DustOptions! Invalid DustOptions object!");
        }
        return null;
    }

    @Override
    public JsonElement serialize(Particle.DustOptions dustOptions, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject dustObj = new JsonObject();
        dustObj.addProperty("size", dustOptions.getSize());
        dustObj.add("color", jsonSerializationContext.serialize(dustOptions.getColor(), Color.class));
        return dustObj;
    }
}
