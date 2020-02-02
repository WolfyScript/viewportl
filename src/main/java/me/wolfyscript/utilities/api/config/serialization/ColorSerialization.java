package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Color;

import java.lang.reflect.Type;

public class ColorSerialization implements JsonSerializer<Color>, JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            int red = ((JsonObject) jsonElement).get("red").getAsInt();
            int green = ((JsonObject) jsonElement).get("green").getAsInt();
            int blue = ((JsonObject) jsonElement).get("blue").getAsInt();
            return Color.fromBGR(blue, green, red);
        } else {
            Main.getMainUtil().sendConsoleWarning("Error Deserializing Color! Invalid Color object!");
        }
        return null;
    }

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject colorObj = new JsonObject();
        colorObj.addProperty("red", color.getRed());
        colorObj.addProperty("green", color.getGreen());
        colorObj.addProperty("blue", color.getBlue());
        return colorObj;
    }
}
