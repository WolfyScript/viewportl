package me.wolfyscript.utilities.api.language;

import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {

    private String lang;
    private YamlConfiguration yamlConfig;

    private HashMap<String, Object> messages;

    public Language(String lang, YamlConfiguration yamlConfig, ConfigAPI configAPI){
        this.lang = lang;
        this.yamlConfig = yamlConfig;
        this.messages = new HashMap<>();
        configAPI.registerConfig(yamlConfig);

        reloadKeys();
    }

    /*
        Only keys with String language settings allowed!

        $general.items.debug$ -> §4Debug
     */
    public String replaceKeys(String msg) {
        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("[$]([a-z0-9._]*?)[$]");
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            keys.add(matcher.group(0));
        }
        for(String key : keys){
            Object object = messages.get(key.replace("$", ""));
            if(object instanceof String){
                msg = msg.replace(key, (String) object);
            }else if(object instanceof List){
                StringBuilder sB = new StringBuilder();
                List<String> list = (List<String>) object;
                list.forEach(s -> sB.append(' ').append(s));
                msg = msg.replace(key, sB.toString());
            }
        }
        return msg;
    }

    public List<String> replaceKeys(String... msg) {
        List<String> list = Arrays.asList(msg);
        for (int i = 0; i < msg.length; i++) {
            list.set(i, replaceKeys(msg[i]));
        }
        return list;
    }

    /*
        Get's the List<String> from the language file for this key!
     */
    public List<String> replaceKey(String key) {
        List<String> message = new ArrayList<>();
        if (key != null){
            if(key.contains("$")){
                key = key.replace("$", "");
            }

            if(messages.get(key) instanceof ArrayList){
                message.addAll((ArrayList<String>) messages.get(key));
            }
        }
        return message;
    }

    public FileConfiguration getYamlConfig(){
        return yamlConfig.getConfig();
    }

    public void reloadKeys(){
        for(String key : this.yamlConfig.getKeys()){
            if(this.yamlConfig.getObject(key) instanceof ArrayList){
                messages.put(key, this.yamlConfig.getConfig().getStringList(key));
            }else{
                messages.put(key, this.yamlConfig.getString(key));
            }
        }
    }

    public String getName() {
        return lang;
    }
}
