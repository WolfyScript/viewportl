/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class LocationSerialization {

    public static void create(SimpleModule module) {

        JacksonUtil.addSerializerAndDeserializer(module, Location.class, (location, gen, serializerProvider) -> {
            gen.writeStartObject();
            gen.writeStringField("world", location.getWorld().getUID().toString());
            gen.writeArrayFieldStart("pos");
            gen.writeNumber(location.getX());
            gen.writeNumber(location.getY());
            gen.writeNumber(location.getZ());
            gen.writeNumber(location.getYaw());
            gen.writeNumber(location.getPitch());
            gen.writeEndArray();
            gen.writeEndObject();
        }, (p, d) -> {
            JsonNode node = p.readValueAsTree();
            var api = WolfyUtilities.getWUCore();
            if (node.isObject()) {
                var uuid = UUID.fromString(node.get("world").asText());
                var world = Bukkit.getWorld(uuid);
                if (world != null) {
                    var jsonNode = node.get("pos");
                    if (jsonNode.size() == 5) {
                        double x = jsonNode.get(0).asDouble();
                        double y = jsonNode.get(1).asDouble();
                        double z = jsonNode.get(2).asDouble();
                        float yaw = jsonNode.get(3).floatValue();
                        float pitch = jsonNode.get(4).floatValue();
                        return new Location(world, x, y, z, yaw, pitch);
                    }
                    api.getConsole().warn("Error Deserializing Location! Invalid Position: expected array size 5 got " + jsonNode.size());
                    return null;
                }
                api.getConsole().warn("Error Deserializing Location! Missing World with uid " + uuid);
                return null;
            }
            api.getConsole().warn("Error Deserializing Location! Invalid Location object!");
            return null;
        });
    }
}
