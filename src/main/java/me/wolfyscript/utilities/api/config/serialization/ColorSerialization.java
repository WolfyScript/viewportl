package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Color;

import java.lang.reflect.Type;

public class ColorSerialization implements JsonSerializer<Color>, JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            int red = ((JsonObject) jsonElement).get("red").getAsByte();
            int green = ((JsonObject) jsonElement).get("green").getAsByte();
            int blue = ((JsonObject) jsonElement).get("blue").getAsByte();
            return Color.fromBGR(red, green, blue);
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
