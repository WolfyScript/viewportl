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
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.util.Vector;

public class VectorSerialization {

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, Vector.class, (value, gen, serializerProvider) -> {
            gen.writeStartArray();
            gen.writeNumber(value.getX());
            gen.writeNumber(value.getY());
            gen.writeNumber(value.getZ());
            gen.writeEndArray();
        }, (p, deserializationContext) -> {
            var api = WolfyUtilities.getWUCore();
            JsonNode node = p.readValueAsTree();
            if (node.isArray()) {
                var arrayNode = (ArrayNode) node;
                return new Vector(arrayNode.get(0).asDouble(0), arrayNode.get(1).asDouble(0), arrayNode.get(2).asDouble(0));
            }
            api.getConsole().warn("Error Deserializing Vector! Invalid Vector object!");
            return null;
        });
    }
}
