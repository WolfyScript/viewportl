package me.wolfyscript.utilities.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Set;

public class JsonConfig {

    private final Gson gson = new GsonBuilder().create();
    private HashMap<String, Object> map = new HashMap<>();

    private ConfigAPI configAPI;
    private Plugin plugin;
    private File configFile;
    private String defPath;
    private String defFileName;

    public JsonConfig(ConfigAPI configAPI, String path, String filename, String defPath, String defFileName, boolean overwrite) {
        this.configAPI = configAPI;
        this.plugin = configAPI.getPlugin();
        this.defPath = defPath;
        this.defFileName = defFileName;
        this.configFile = new File(path, filename + ".json");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();

            loadDefaults(overwrite);
            onFirstInit();
        }
        load();
    }

    public void onFirstInit() {

    }

    public void init() {

    }

    public void loadDefaults(boolean overwrite) {
        if(!defPath.isEmpty() && !defFileName.isEmpty()){
            if (plugin.getResource(defPath + "/" + defFileName + ".json") != null) {
                plugin.saveResource(defPath + "/" + defFileName + ".json", overwrite);
            }
        }
    }

    public String toString(boolean prettyPrinting) {
        Gson gsonBuilder = prettyPrinting ? gson : new GsonBuilder().create();
        return gsonBuilder.toJson(map);
    }

    public String toString() {
        return toString(false);
    }

    public void load() {
        try {
            map = gson.fromJson(new FileReader(this.configFile), new HashMap<String, Object>().getClass());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(boolean prettyPrinting) {
        final String json = toString(prettyPrinting);
        configFile.delete();
        try {
            Files.write(configFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        save(false);
    }

    public void reload() {
        save();
        load();
    }

    public HashMap<String, Object> getValues() {
        return map;
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

    public Object getObject(String key) {
        return map.get(key);
    }

}
