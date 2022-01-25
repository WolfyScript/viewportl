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

import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageAPI {

    private static final String NAME_KEY = ".name";
    private static final String LORE_KEY = ".lore";
    private static final String BUTTON_WINDOW_KEY = "inventories.%s.%s.items.%s";
    private static final String BUTTON_CLUSTER_KEY = "inventories.%s.global_items.%s";

    private final WolfyUtilities api;

    private final List<Language> languages;

    private Language activeLanguage;
    private Language fallbackLanguage;

    public LanguageAPI(WolfyUtilities api) {
        this.api = api;
        this.languages = new ArrayList<>();
        this.activeLanguage = null;
        this.fallbackLanguage = null;
    }

    public void unregisterLanguages() {
        languages.clear();
    }

    /**
     * Registers a new Language.
     * If no active Language is set this language will be used as the active language.
     *
     * @param language the Language to register
     */
    public void registerLanguage(Language language) {
        if(activeLanguage == null){
            setActiveLanguage(language);
        }
        if (fallbackLanguage == null) {
            setFallbackLanguage(language);
        }
        if (!languages.contains(language)) {
            languages.add(language);
        }
    }

    /**
     * Sets the Language as the actively used Language.
     *
     * @param language
     */
    public void setActiveLanguage(Language language) {
        activeLanguage = language;
    }

    public Language getActiveLanguage() {
        return activeLanguage;
    }

    /**
     * Sets the Fallback Language which is used if an key isn't found in the active Language.
     * <p>
     * For example if a Button isn't configured in the active Language it will look for it
     * in the fallback language and use it if available.
     *
     * @param fallbackLanguage
     */
    public void setFallbackLanguage(Language fallbackLanguage) {
        this.fallbackLanguage = fallbackLanguage;
    }

    public Language getFallbackLanguage() {
        return fallbackLanguage;
    }

    private JsonNode getNodeAt(String path) {
        JsonNode node = getActiveLanguage().getNodeAt(path);
        if(node.isMissingNode()){
            node = getFallbackLanguage().getNodeAt(path);
        }
        return node;
    }

    public String replaceKeys(String msg) {
        Matcher matcher = Pattern.compile("[$]([a-zA-Z0-9._]*?)[$]").matcher(msg);
        while (matcher.find()) {
            String key = matcher.group(0);
            JsonNode node = getNodeAt(key.replace("$", ""));
            if(node.isTextual()){
                msg = msg.replace(key, node.asText());
            }else if(node.isArray()){
                StringBuilder sB = new StringBuilder();
                node.elements().forEachRemaining(n -> sB.append(' ').append(n.asText()));
                msg = msg.replace(key, sB.toString());
            }
        }
        return msg;
    }

    public List<String> replaceKeys(List<String> msg) {
        Pattern pattern = Pattern.compile("[$]([a-zA-Z0-9._]*?)[$]");
        List<String> result = new ArrayList<>();
        msg.forEach(s -> {
            List<String> keys = new ArrayList<>();
            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                keys.add(matcher.group(0));
            }
            if (keys.size() > 1) {
                for (String key : keys) {
                    JsonNode node = getNodeAt(key.replace("$", ""));
                    if (node.isTextual()) {
                        result.add(ChatColor.convert(s.replace(key, node.asText())));
                    } else if (node.isArray()) {
                        StringBuilder sB = new StringBuilder();
                        node.elements().forEachRemaining(n -> sB.append(' ').append(n.asText()));
                        result.add(ChatColor.convert(s.replace(key, sB.toString())));
                    }
                }
            } else if (!keys.isEmpty()) {
                String key = keys.get(0);
                JsonNode node = getNodeAt(key.replace("$", ""));
                if (node.isTextual()) {
                    result.add(ChatColor.convert(s.replace(key, node.asText())));
                } else if (node.isArray()) {
                    node.elements().forEachRemaining(n -> result.add(n.asText()));
                }
            } else {
                result.add(ChatColor.convert(s));
            }
        });
        return result;
    }

    public List<String> replaceKeys(String... msg) {
        return Arrays.stream(msg).map(this::replaceKeys).collect(Collectors.toList());
    }

    public String replaceColoredKeys(String msg) {
        return ChatColor.convert(replaceKeys(msg));
    }

    public List<String> replaceColoredKeys(List<String> msg) {
        return replaceKeys(msg).stream().map(ChatColor::convert).collect(Collectors.toList());
    }

    public List<String> replaceKey(String key) {
        return readKey(key, JsonNode::asText);
    }

    public List<String> replaceColoredKey(String key) {
        return readKey(key, node -> ChatColor.convert(node.asText()));
    }

    public <T> List<T> readKey(String key, Function<JsonNode, T> nodeMapper) {
        List<T> results = new ArrayList<>();
        if (key != null) {
            JsonNode node = getNodeAt(key.replace("$", ""));
            if (node.isArray()) {
                node.elements().forEachRemaining(n -> results.add(nodeMapper.apply(n)));
            }
        }
        return results;
    }

    public String getButtonName(NamespacedKey window, String buttonKey) {
        return replaceColoredKeys("$" + String.format(BUTTON_WINDOW_KEY + NAME_KEY, window.getNamespace(), window.getKey(), buttonKey) + "$");
    }

    public String getButtonName(String clusterId, String buttonKey) {
        return replaceColoredKeys("$" + String.format(BUTTON_CLUSTER_KEY + NAME_KEY, clusterId, buttonKey) + "$");
    }

    public List<String> getButtonLore(NamespacedKey window, String buttonKey) {
        return replaceColoredKey(String.format(BUTTON_WINDOW_KEY + LORE_KEY, window.getNamespace(), window.getKey(), buttonKey));
    }

    public List<String> getButtonLore(String clusterId, String buttonKey) {
        return replaceColoredKey(String.format(BUTTON_CLUSTER_KEY + LORE_KEY, clusterId, buttonKey));
    }

    public Component getComponent(String key) {
        JsonNode node = getNodeAt(key);
        if (node.isTextual()) {
            return api.getChat().getMiniMessage().parse(node.asText());
        } else if(node.isArray()) {
            Component component = Component.empty();
            Iterator<JsonNode> nodeItr = node.elements();
            while (nodeItr.hasNext()) {
                JsonNode jsonNode = nodeItr.next();
                component.append(api.getChat().getMiniMessage().parse(jsonNode.asText()));
                if (nodeItr.hasNext()) {
                    component.append(Component.text(" "));
                }
            }
            return component;
        }
        return Component.empty();
    }

    public Component getComponent(String key, List<Template> templates) {
        JsonNode node = getNodeAt(key);
        if (node.isTextual()) {
            return api.getChat().getMiniMessage().parse(node.asText(), templates);
        } else if(node.isArray()) {
            Component component = Component.empty();
            Iterator<JsonNode> nodeItr = node.elements();
            while (nodeItr.hasNext()) {
                JsonNode jsonNode = nodeItr.next();
                component.append(api.getChat().getMiniMessage().parse(jsonNode.asText(), templates));
                if (nodeItr.hasNext()) {
                    component.append(Component.text(" "));
                }
            }
            return component;
        }
        return Component.empty();
    }

}
