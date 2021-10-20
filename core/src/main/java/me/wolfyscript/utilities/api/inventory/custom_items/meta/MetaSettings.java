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

package me.wolfyscript.utilities.api.inventory.custom_items.meta;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@JsonDeserialize(using = MetaSettings.Deserializer.class)
public class MetaSettings extends HashMap<NamespacedKey, Meta> {

    public MetaSettings() {
        Registry.META_PROVIDER.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue().provide()));
    }

    public MetaSettings(JsonNode node) {
        this();
        if (node != null) {
            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey().toLowerCase(Locale.ROOT);
                NamespacedKey namespacedKey = key.contains(":") ? NamespacedKey.of(key) : NamespacedKey.wolfyutilties(key);
                Meta.Provider<?> provider = Registry.META_PROVIDER.get(namespacedKey);
                if (provider != null) {
                    Meta meta;
                    if (entry.getValue().isTextual()) {
                        meta = provider.provide();
                        meta.setOption(JacksonUtil.getObjectMapper().convertValue(entry.getValue(), MetaSettings.Option.class));
                    } else {
                        meta = provider.parse(entry.getValue());
                    }
                    put(namespacedKey, meta);
                }
            });
        }
    }

    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        return values().stream().allMatch(meta -> meta.check(itemOther, item));
    }

    public enum Option {
        EXACT, IGNORE, HIGHER, HIGHER_EXACT, LOWER, LOWER_EXACT, HIGHER_LOWER
    }

    public static class Deserializer extends StdDeserializer<MetaSettings> {

        public Deserializer() {
            super(MetaSettings.class);
        }

        @Override
        public MetaSettings deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isTextual()) {
                //Old String style meta
                node = JacksonUtil.getObjectMapper().readTree(node.asText());
            }
            //New Json style meta
            return new MetaSettings(node);
        }

        protected Deserializer(Class<MetaSettings> t) {
            super(t);
        }
    }
}
