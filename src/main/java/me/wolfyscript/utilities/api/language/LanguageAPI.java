package me.wolfyscript.utilities.api.language;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageAPI {

    private Plugin plugin;

    private ArrayList<Language> languages;

    private Language activeLanguage;

    //private HashMap<String, Language> playerLanguage;

    public LanguageAPI(Plugin plugin) {
        this.plugin = plugin;
        this.languages = new ArrayList<>();
        this.activeLanguage = null;
        //this.playerLanguage = new HashMap<>();
    }

    public void unregisterLanguages() {
        languages.clear();
    }

    public void registerLanguage(Language language) {
        if (languages.isEmpty()) {
            setActiveLanguage(language);
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

    public String replaceKeys(String msg) {
        return getActiveLanguage().replaceKeys(msg);
    }

    public String replaceColoredKeys(String msg){
        return getActiveLanguage().replaceColoredKeys(msg);
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
