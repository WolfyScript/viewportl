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
import com.google.common.collect.Streams;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class APIReferenceSerialization {

    private static final String CUSTOM_AMOUNT = "custom_amount";
    private static final String WEIGHT = "weight";

    private APIReferenceSerialization() {
    }

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, APIReference.class, (value, gen, provider) -> {
            gen.writeStartObject();
            gen.writeNumberField(CUSTOM_AMOUNT, value.getAmount());
            if (value.getWeight() > 0) {
                gen.writeNumberField(WEIGHT, value.getWeight());
            }
            value.serialize(gen, provider);
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                for (String key : Streams.stream(node.fieldNames()).filter(s -> !s.equals(WEIGHT) && !s.equals(CUSTOM_AMOUNT)).collect(Collectors.toSet())) {
                    APIReference.Parser<?> parser = CustomItem.getApiReferenceParser(key);
                    if (parser != null) {
                        JsonNode element = node.path(key);
                        if (element != null) {
                            APIReference reference = parser.parse(element);
                            if (reference != null) {
                                reference.setAmount(node.path(CUSTOM_AMOUNT).asInt(0));
                                reference.setWeight(node.path(WEIGHT).asDouble(0));
                                return reference;
                            }
                        }
                    }
                }
            } else if (node.isTextual()) {
                //Legacy items saved as string!
                APIReference apiReference = JacksonUtil.getObjectMapper().treeToValue(node, VanillaRef.class);
                if (apiReference != null) {
                    return apiReference;
                }
            }
            return new VanillaRef(new ItemStack(Material.AIR));
        });
    }
}
