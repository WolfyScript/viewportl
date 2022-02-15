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
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.JsonConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language extends JsonConfig<Language> {

    private final WolfyUtilities api;
    private final String lang;
    private final Type type = Type.NESTED;
    private final Map<String, LanguageNode> mappedLangNodes = new HashMap<>();

    @JsonCreator
    public Language(@JsonProperty("api") @JacksonInject("api")  WolfyUtilities api, @JsonProperty("lang") @JacksonInject("lang") String lang) {
        super(new File(api.getPlugin().getDataFolder(), "lang/" + lang + ".json"), Language.class);
        this.lang = lang;
        this.api = api;
    }

    public Language(Plugin plugin, String lang) {
        super(new File(plugin.getDataFolder(), "lang/" + lang + ".json"), Language.class);
        this.lang = lang;
        this.api = WolfyUtilCore.getInstance().getAPI(plugin);
    }

    @JsonAnySetter
    private void setValues(Map<String, JsonNode> nodes) {
        if (type.equals(Type.NESTED)) {
            nodes.forEach((fieldName, jsonNode) -> readNestedNode(new StringBuilder(fieldName), jsonNode));
        } else {
            //Support for flatten languages
        }
    }

    private void readNestedNode(StringBuilder path, JsonNode node) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> readNestedNode(path.append(".").append(entry.getKey()), entry.getValue()));
        } else if (node.isArray()) {
            mappedLangNodes.put(path.toString(), new LanguageNodeArray(api.getChat(), node));
        } else {
            mappedLangNodes.put(path.toString(), new LanguageNodeText(api.getChat(), node));
        }
    }

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

    public enum Type {
        NESTED,
        FLAT

    }

}
