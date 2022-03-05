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

package me.wolfyscript.utilities.api.language;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.JsonConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents a single Language that can be loaded from a JSON file.<br>
 * You can load and register a language using {@link LanguageAPI#loadLangFile(String)}
 *
 * <p>
 * <strong>It only extends the {@link JsonConfig} for backwards compatibility, so in future it will get removed!</strong><br>
 * Instead of creating it via the old constructor you need to use the {@link LanguageAPI#loadLangFile(String)} method!
 * </p>
 */
public class Language extends JsonConfig<JsonNode> {

    @JsonIgnore
    private final WolfyUtilities api;
    @JsonIgnore
    private final String lang;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final Type type = Type.NESTED;
    @JsonIgnore
    private final Map<String, LanguageNode> mappedLangNodes = new ConcurrentHashMap<>();

    /**
     * Used to create (deserialize) the Language from Json. The {@link LanguageAPI} provides the option to load the Language from a file.
     *
     * @param file The file of the language (Only necessary for backwards compatibility!)
     * @param api The api this Language belongs to.
     * @param lang The language name.
     * @see LanguageAPI#loadLangFile(String)
     */
    @JsonCreator
    protected Language(@JsonProperty("file") @JacksonInject("file") File file, @JacksonInject("api")  WolfyUtilities api, @JsonProperty("lang") @JacksonInject("lang") String lang) {
        super(file, JsonNode.class);
        this.lang = lang;
        this.api = api;
    }

    /**
     * Creates a new Language and loads it from the file. <br>
     * <strong>Only used for backwards compatibility!</strong>
     *
     * @deprecated {@link LanguageAPI#loadLangFile(String)} should be used instead!
     * @param plugin
     * @param lang
     */
    @Deprecated
    public Language(Plugin plugin, String lang) {
        super(new File(plugin.getDataFolder(), "lang/" + lang + ".json"), JsonNode.class);
        this.lang = lang;
        this.api = WolfyUtilCore.getInstance().getAPI(plugin);
        //Make sure Languages created the old way are still setting the values.
        setValues(Streams.stream(value.fields()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @JsonAnySetter
    private void setValues(Map<String, JsonNode> nodes) {
        switch (type) {
            case NESTED -> nodes.forEach(this::readNestedNode);
            case FLAT -> nodes.forEach((path, jsonNode) -> {
                if (jsonNode.isArray()) {
                    mappedLangNodes.put(path, new LanguageNodeArray(api.getChat(), jsonNode));
                } else {
                    mappedLangNodes.put(path, new LanguageNodeText(api.getChat(), jsonNode));
                }
            });
            default -> { /* Not going to happen */ }
        }
    }

    /**
     * Always saves the language in a semi-flat style.
     * In the {@link Type#FLAT} style.
     *
     * @return The semi-flat representation of the language.
     */
    @JsonAnyGetter
    private Map<String, LanguageNode> getValues() {
        mappedLangNodes.values().removeIf(LanguageNodeMissing.class::isInstance);
        return mappedLangNodes;
    }

    private void readNestedNode(String path, JsonNode node) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> readNestedNode(path + "." + entry.getKey(), entry.getValue()));
        } else if (node.isArray()) {
            mappedLangNodes.put(path, new LanguageNodeArray(api.getChat(), node));
        } else {
            mappedLangNodes.put(path, new LanguageNodeText(api.getChat(), node));
        }
    }

    /**
     * Gets the name of this language.
     *
     * @return The name of the language.
     */
    @JsonIgnore
    public String getName() {
        return lang;
    }

    @JsonIgnore
    public String getVersion() {
        return mappedLangNodes.get("version").getRaw();
    }

    @JsonIgnore
    public List<String> getAuthors() {
        List<String> list = new ArrayList<>();
        JsonNode node = getNodeAt("author");
        if (node.isTextual()) {
            list.add(node.asText());
        } else {
            node.elements().forEachRemaining(n -> list.add(n.asText()));
        }
        return list;
    }

    @NotNull
    public LanguageNode getNode(String path) {
        return mappedLangNodes.computeIfAbsent(path, s -> new LanguageNodeMissing(api.getChat()));
    }

    @NotNull
    public JsonNode getNodeAt(String path) {
        return getNode(path).getValue();
    }

    /**
     * The type in which the language data is provided.
     */
    public enum Type {
        /**
         * Is the normal JSON format you are used to.
         * <pre>
         *  {
         *      "an" : {
         *          "nested" : {
         *              "value": 2,
         *              "array" : [
         *                  "element",
         *                  "element1",
         *                  "element2"
         *              ]
         *          }
         *      }
         *  }</pre>
         */
        NESTED,
        /**
         * Flattens every object using the '.' path separator.
         * Arrays are kept as is and not split into separate fields.
         * <pre>
         *  {
         *      "an.nested.value": 2,
         *      "an.nested.array" : [
         *          "element",
         *          "element1",
         *          "element2"
         *      ]
         *  }</pre>
         */
        FLAT

    }

}
