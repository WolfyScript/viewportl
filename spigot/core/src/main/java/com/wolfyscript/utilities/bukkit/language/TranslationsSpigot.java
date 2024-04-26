package com.wolfyscript.utilities.bukkit.language;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import com.wolfyscript.utilities.language.Language;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.wolfyscript.utilities.language.Translations;
import org.jetbrains.annotations.NotNull;

public class TranslationsSpigot extends Translations {

    public TranslationsSpigot(WolfyUtils api) {
        super(api);
    }

    public Language loadLangFile(String lang) {
        var file = getLangFile(lang);
        if (!file.exists()) {
            try {
                api.exportResource("lang/" + lang + ".json", file, true);
            } catch (IllegalArgumentException ex) {
                api.getLogger().severe("Couldn't load lang \""+lang+"\"! Language resource doesn't exists!");
                return null;
            }
        }
        var injectableValues = new InjectableValues.Std();
        injectableValues.addValue("file", file);
        injectableValues.addValue("api", api);
        injectableValues.addValue("lang", lang);
        try {
            Language language = api.getJacksonMapperUtil().getGlobalMapper().reader(injectableValues).readValue(file, Language.class);
            registerLanguage(language);
            return language;
        } catch (IOException ex) {
            api.getLogger().log(Level.SEVERE, "Couldn't load language \""+lang+"\"!");
            ex.printStackTrace();
        }
        return null;
    }

    public void saveLangFile(@NotNull Language language) {
        try {
            api.getJacksonMapperUtil().getGlobalMapper().writeValue(getLangFile(language.getName()), language);
        } catch (IOException ex) {
            api.getLogger().severe("Couldn't save language \""+language.getName()+"\"!");
            api.getLogger().throwing("LanguageAPI", "saveLangFile", ex);
        }

    }

    protected File getLangFile(String lang) {
        return new File(api.getDataFolder(), "lang/" + lang + ".json");
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
                        result.add(s.replace(key, node.asText()));
                    } else if (node.isArray()) {
                        StringBuilder sB = new StringBuilder();
                        node.elements().forEachRemaining(n -> sB.append(' ').append(n.asText()));
                        result.add(s.replace(key, sB.toString()));
                    }
                }
            } else if (!keys.isEmpty()) {
                String key = keys.get(0);
                JsonNode node = getNodeAt(key.replace("$", ""));
                if (node.isTextual()) {
                    result.add(s.replace(key, node.asText()));
                } else if (node.isArray()) {
                    node.elements().forEachRemaining(n -> result.add(n.asText()));
                }
            } else {
                result.add(s);
            }
        });
        return result;
    }

    @Override
    public String convertLegacyToMiniMessage(String legacyText) {
        String rawLegacy = ChatColor.convert(legacyText);
        return rawLegacy;
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

}
