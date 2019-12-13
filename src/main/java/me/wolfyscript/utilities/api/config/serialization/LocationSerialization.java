package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationSerialization implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement instanceof JsonObject){
            UUID uuid = UUID.fromString(((JsonObject) jsonElement).getAsJsonPrimitive("world").getAsString());
            World world = Bukkit.getWorld(uuid);
            if(world != null){
                JsonArray jsonArray = ((JsonObject) jsonElement).getAsJsonArray("pos");
                if(jsonArray.size() == 5){
                    double x = jsonArray.get(0).getAsDouble();
                    double y = jsonArray.get(1).getAsDouble();
                    double z = jsonArray.get(2).getAsDouble();
                    float yaw = jsonArray.get(3).getAsFloat();
                    float pitch = jsonArray.get(4).getAsFloat();
                    return new Location(world, x, y, z, yaw, pitch);
                }else{
                    Main.getMainUtil().sendConsoleWarning("Error Deserializing Location! Invalid Position: expected array size 5 got "+jsonArray.size());
                }
            }else{
                Main.getMainUtil().sendConsoleWarning("Error Deserializing Location! Missing World with uid "+uuid.toString());
            }
        }else{
            Main.getMainUtil().sendConsoleWarning("Error Deserializing Location! Invalid Location object!");
        }
        return null;
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject locObject = new JsonObject();
        locObject.addProperty("world", location.getWorld().getUID().toString());
        JsonArray posObject = new JsonArray();
        posObject.add(location.getX());
        posObject.add(location.getY());
        posObject.add(location.getZ());
        posObject.add(location.getYaw());
        posObject.add(location.getPitch());
        locObject.add("pos", posObject);
        return locObject;
    }
}
