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

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageAPI {

    private final WolfyUtilities api;

    private final Map<String, Language> registeredLanguages = new HashMap<>();
    private Language activeLanguage;
    private Language fallbackLanguage;

    public LanguageAPI(WolfyUtilities api) {
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

    public Language loadLangFile(String lang) {
        var file = getLangFile(lang);
        if (!file.exists()) {
            try {
                api.getPlugin().saveResource("lang/" + lang + ".json", true);
            } catch (IllegalArgumentException ex) {
                api.getConsole().getLogger().severe("Couldn't load lang \""+lang+"\"! Language resource doesn't exists!");
                return null;
            }
        }
        var injectableValues = new InjectableValues.Std();
        injectableValues.addValue("file", file);
        injectableValues.addValue("api", api);
        injectableValues.addValue("lang", lang);
        try {
            Language language = JacksonUtil.getObjectMapper().reader(injectableValues).readValue(file, Language.class);
            registerLanguage(language);
            return language;
        } catch (IOException ex) {
            api.getConsole().getLogger().log(Level.SEVERE, "Couldn't load language \""+lang+"\"!");
            ex.printStackTrace();
        }
        return null;
    }

    public void saveLangFile(@NotNull Language language) {
        try {
            JacksonUtil.getObjectMapper().writeValue(getLangFile(language.getName()), language);
        } catch (IOException ex) {
            api.getConsole().getLogger().severe("Couldn't save language \""+language.getName()+"\"!");
            api.getConsole().getLogger().throwing("LanguageAPI", "saveLangFile", ex);
        }

    }

    private File getLangFile(String lang) {
        return new File(api.getPlugin().getDataFolder(), "lang/" + lang + ".json");
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
        return getNode(path).getValue();
    }

    private LanguageNode getNode(String path) {
        LanguageNode node = getActiveLanguage().getNode(path);
        if(node instanceof LanguageNodeMissing){
            node = getFallbackLanguage().getNode(path);
        }
        return node;
    }

    public Component getComponent(String key) {
        return getComponent(key, false, List.of());
    }

    public Component getComponent(String key, boolean translateLegacyColor) {
        return getComponent(key, translateLegacyColor, List.of());
    }

    public Component getComponent(String key, List<? extends TagResolver> resolvers) {
        return getComponent(key, false, resolvers);
    }

    public Component getComponent(String key, boolean translateLegacyColor, List<? extends TagResolver> resolvers) {
        return getNode(key).getComponent(translateLegacyColor, resolvers);
    }

    public List<Component> getComponents(String key) {
        return getComponents(key, false, List.of());
    }

    public List<Component> getComponents(String key, boolean translateLegacyColor) {
        return getComponents(key, translateLegacyColor, List.of());
    }

    public List<Component> getComponents(String key, List<? extends TagResolver> resolvers) {
        return getComponents(key, false, resolvers);
    }

    public List<Component> getComponents(String key, boolean translateLegacyColor, List<? extends TagResolver> resolvers) {
        return getNode(key).getComponents(translateLegacyColor, resolvers);
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

    @Deprecated
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

    @Deprecated
    public List<String> replaceKeys(String... msg) {
        return Arrays.stream(msg).map(this::replaceKeys).collect(Collectors.toList());
    }

    @Deprecated
    public String replaceColoredKeys(String msg) {
        return ChatColor.convert(replaceKeys(msg));
    }

    @Deprecated
    public List<String> replaceColoredKeys(List<String> msg) {
        return replaceKeys(msg).stream().map(ChatColor::convert).collect(Collectors.toList());
    }

    @Deprecated
    public List<String> replaceKey(String key) {
        return readKey(key, JsonNode::asText);
    }

    @Deprecated
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
        return BukkitComponentSerializer.legacy().serialize(getComponent(String.format(ButtonState.BUTTON_WINDOW_KEY + ButtonState.NAME_KEY, window.getNamespace(), window.getKey(), buttonKey), true));
    }

    public String getButtonName(String clusterId, String buttonKey) {
        return BukkitComponentSerializer.legacy().serialize(getComponent(String.format(ButtonState.BUTTON_CLUSTER_KEY + ButtonState.NAME_KEY, clusterId, buttonKey), true));
    }

    public List<String> getButtonLore(NamespacedKey window, String buttonKey) {
        return getComponents(String.format(ButtonState.BUTTON_WINDOW_KEY + ButtonState.NAME_KEY, window.getNamespace(), window.getKey(), buttonKey), true).stream().map(component -> BukkitComponentSerializer.legacy().serialize(component)).collect(Collectors.toList());
    }

    public List<String> getButtonLore(String clusterId, String buttonKey) {
        return getComponents(String.format(ButtonState.BUTTON_CLUSTER_KEY + ButtonState.LORE_KEY, clusterId, buttonKey), true).stream().map(component -> BukkitComponentSerializer.legacy().serialize(component)).collect(Collectors.toList());
    }

}
