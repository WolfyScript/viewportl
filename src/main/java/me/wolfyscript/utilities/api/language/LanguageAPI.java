package me.wolfyscript.utilities.api.language;

import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageAPI {

    private final Plugin plugin;

    private final ArrayList<Language> languages;

    private Language activeLanguage;
    private Language fallbackLanguage;

    //private HashMap<String, Language> playerLanguage;

    public LanguageAPI(Plugin plugin) {
        this.plugin = plugin;
        this.languages = new ArrayList<>();
        this.activeLanguage = null;
        this.fallbackLanguage = null;
        //this.playerLanguage = new HashMap<>();
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
        if(fallbackLanguage == null){
            setFallbackLanguage(language);
        }
        if (!languages.contains(language)) {
            languages.add(language);
        }
    }

    public void setActiveLanguage(Language language) {
        activeLanguage = language;
    }

    public Language getActiveLanguage() {
        return activeLanguage;
    }

    public void setFallbackLanguage(Language fallbackLanguage) {
        this.fallbackLanguage = fallbackLanguage;
    }

    public Language getFallbackLanguage() {
        return fallbackLanguage;
    }

    private JsonNode getNodeAt(String path){
        JsonNode node = getActiveLanguage().getNodeAt(path);
        if(node.isMissingNode()){
            node = getFallbackLanguage().getNodeAt(path);
        }
        return node;
    }

    public String replaceKeys(String msg) {
        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("[$]([a-zA-Z0-9._]*?)[$]");
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            keys.add(matcher.group(0));
        }
        for (String key : keys) {
            JsonNode node = getNodeAt(key.replace("$", ""));
            if(node.isTextual()){
                return msg.replace(key, node.asText());
            }else if(node.isArray()){
                StringBuilder sB = new StringBuilder();
                node.elements().forEachRemaining(n -> sB.append(' ').append(n.asText()));
                return msg.replace(key, sB.toString());
            }
        }
        return msg;
    }

    public String replaceColoredKeys(String msg){
        return WolfyUtilities.translateColorCodes(replaceKeys(msg));
    }

    public List<String> replaceColoredKeys(List<String> msg) {
        return replaceKeys(msg).stream().map(WolfyUtilities::translateColorCodes).collect(Collectors.toList());
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
                    if(node.isTextual()){
                        result.add(WolfyUtilities.translateColorCodes(s.replace(key, node.asText())));
                    }else if(node.isArray()){
                        StringBuilder sB = new StringBuilder();
                        node.elements().forEachRemaining(n -> sB.append(' ').append(n.asText()));
                        result.add(WolfyUtilities.translateColorCodes(s.replace(key, sB.toString())));
                    }
                }
            } else {
                String key = keys.get(0);
                JsonNode node = getNodeAt(key.replace("$", ""));
                if (node.isTextual()) {
                    result.add(WolfyUtilities.translateColorCodes(s.replace(key, node.asText())));
                } else if (node.isArray()) {
                    node.elements().forEachRemaining(n -> result.add(n.asText()));
                }
            }
        });
        return result;
    }

    public List<String> replaceKeys(String... msg) {
        return Arrays.stream(msg).map(this::replaceKeys).collect(Collectors.toList());
    }

    /*
        Get's the List<String> from the language file for this key!
     */
    public List<String> replaceKey(String key) {
        List<String> message = new ArrayList<>();
        if (key != null) {
            JsonNode node = getNodeAt(key.replace("$", ""));
            if (node.isArray()) {
                node.elements().forEachRemaining(n -> message.add(n.asText()));
                //message.addAll(Streams.stream(node.elements()).map(n -> n.asText()).collect(Collectors.toSet()));
            }
        }
        return message;
    }

    /*
    public void setPlayerLanguage(Player player, Language language){
        playerLanguage.put(player.getUniqueId().toString(), language);
    }

    public Language getPlayerLanguage(Player player){
        return playerLanguage.get(player.getUniqueId().toString());
    }

    */
}
