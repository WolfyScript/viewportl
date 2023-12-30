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

package com.wolfyscript.utilities.language;

import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.WolfyUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LanguageAPI {

    protected final WolfyUtils api;

    private final Map<String, Language> registeredLanguages = new HashMap<>();
    private Language activeLanguage;
    private Language fallbackLanguage;

    public LanguageAPI(WolfyUtils api) {
        this.api = api;
        this.activeLanguage = null;
        this.fallbackLanguage = null;
    }

    public void unregisterLanguages() {
        registeredLanguages.clear();
    }

    public Language getLanguage(String lang) {
        return registeredLanguages.get(lang);
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
        registeredLanguages.putIfAbsent(language.getName(), language);
    }

    public abstract Language loadLangFile(String lang);

    public abstract void saveLangFile(@NotNull Language language);

    protected abstract File getLangFile(String lang);

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

    protected JsonNode getNodeAt(String path) {
        return getNode(path).getValue();
    }

    protected LanguageNode getNode(String path) {
        LanguageNode node = getActiveLanguage().getNode(path);
        if(node instanceof LanguageNodeMissing){
            node = getFallbackLanguage().getNode(path);
        }
        return node;
    }

    /**
     * Retrieves the Component of the specified key from the active language.<br>
     * In case the key cannot be found in the active language, it retrieves it from the fallback language, if available.<br>
     * Otherwise, returns an empty Component.
     *
     * @param key The key to the text in the language.
     * @return The Component of the key, or an empty Component if not found.
     */
    public Component getComponent(String key) {
        return getNode(key).getComponent();
    }

    /**
     * Retrieves the Component of the specified key from the active language.<br>
     * In case the key cannot be found in the active language, it retrieves it from the fallback language, if available.<br>
     * Otherwise, returns an empty Component.<br>
     * The TagResolvers can be used to handle custom tags the component might need.
     *
     * @param key The key to the text in the language.
     * @return The Component of the key, or an empty Component if not found.
     */
    public Component getComponent(String key, TagResolver... resolvers) {
        return getNode(key).getComponent(resolvers);
    }

    /**
     * Retrieves the Components of the specified key from the active language.<br>
     * In case the key cannot be found in the active language, it retrieves it from the fallback language, if available.<br>
     * Otherwise, returns an empty List.
     *
     * @param key The key to the text in the language.
     * @return The Component of the key, or an empty List if not found.
     */
    public List<Component> getComponents(String key) {
        return getNode(key).getComponents();
    }

    /**
     * Retrieves the Components of the specified key from the active language.<br>
     * In case the key cannot be found in the active language, it retrieves it from the fallback language, if available.<br>
     * Otherwise, returns an empty List.<br>
     * The TagResolvers can be used to handle custom tags the components might need.
     *
     * @param key The key to the text in the language.
     * @return The Component of the key, or an empty List if not found.
     */
    public List<Component> getComponents(String key, TagResolver... resolvers) {
        return getNode(key).getComponents(resolvers);
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

    /**
     * Converts a message using legacy color codes (§ or &) to the MiniMessage format.
     *
     * @param legacyText The legacy text that may include legacy color codes.
     * @return The converted text compatible with MiniMessage.
     */
    public abstract String convertLegacyToMiniMessage(String legacyText);

    @Deprecated
    public abstract List<String> replaceKeys(List<String> msg);

    @Deprecated
    public abstract List<String> replaceKeys(String... msg);

    @Deprecated
    public abstract String replaceColoredKeys(String msg);

    @Deprecated
    public abstract List<String> replaceColoredKeys(List<String> msg);

    @Deprecated
    public abstract List<String> replaceKey(String key);

    @Deprecated
    public abstract List<String> replaceColoredKey(String key);

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

}
