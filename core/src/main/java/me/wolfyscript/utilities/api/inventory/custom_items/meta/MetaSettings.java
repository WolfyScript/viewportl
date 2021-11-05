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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

@JsonDeserialize(using = MetaSettings.Deserializer.class)
public class MetaSettings extends HashMap<NamespacedKey, Meta> {

    private static final String CHECKS_KEY = "checks";

    private final List<Meta> checks;

    /**
     * Creates a new settings object with an empty list of checks.
     */
    public MetaSettings() {
        checks = new ArrayList<>();
    }

    /**
     * This constructor is only used for serializing the object from a JsonNode.
     * It will convert the old data to the new system if necessary.
     *
     * @param jsonNode The {@link JsonNode} to load the data from.
     */
    private MetaSettings(JsonNode jsonNode) {
        if (jsonNode instanceof ObjectNode node) {
            if (!node.has(CHECKS_KEY)) {
                checks = new ArrayList<>();
                //Convert old meta to new format.
                node.fields().forEachRemaining(entry -> {
                    if(entry.getValue() instanceof ObjectNode value) {
                        String key = entry.getKey().toLowerCase(Locale.ROOT);
                        NamespacedKey namespacedKey = key.contains(":") ? NamespacedKey.of(key) : NamespacedKey.wolfyutilties(key);
                        if (namespacedKey != null) {
                            value.put("key", String.valueOf(namespacedKey));
                            Meta meta = JacksonUtil.getObjectMapper().convertValue(value, Meta.class);
                            if (meta != null && !meta.getOption().equals(Option.IGNORE)) {
                                checks.add(meta);
                            }
                        }
                    }
                });
            } else {
                JsonNode checksNode = node.get(CHECKS_KEY);
                checks = JacksonUtil.getObjectMapper().convertValue(checksNode, new TypeReference<>() {});
            }
        } else {
            checks = new ArrayList<>();
        }
    }

    /**
     * @param meta The {@link Meta} to add to the list of checks. Cannot be null, and must not have the {@link Option#IGNORE}, or it will throw an exception!
     */
    public void addCheck(@NotNull Meta meta) {
        Objects.requireNonNull(meta, "Meta check cannot be null!");
        Preconditions.checkArgument(!meta.getOption().equals(Option.IGNORE), "Deprecated option! Ignored check cannot be added!");
        checks.add(meta);
    }

    public List<Meta> getChecks() {
        return List.copyOf(checks);
    }

    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        return values().stream().allMatch(meta -> meta.check(itemOther, item));
    }

    public enum Option {
        EXACT,
        /**
         * This option was originally used to indicate if the meta check should be active.
         * Now it is no longer used (Only to convert old data), because checks that are not added to the settings are well... not checked anyway.
         */
        @Deprecated(forRemoval = true) IGNORE,
        HIGHER,
        HIGHER_EXACT,
        LOWER,
        LOWER_EXACT,
        HIGHER_LOWER
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
