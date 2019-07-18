package me.wolfyscript.utilities.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private String defJsonString;

    /*
        Creates a json Config file. The default is set inside of the jar at the specified path.
        path - the config file path
        file - config file name without ".json"
        defPath - file path of the file containing the defaults
        defFileName - file name of the file containing the defaults
        overwrite - set if the existing file should be replaced by the defaults
     */
    public JsonConfig(ConfigAPI configAPI, String path, String filename, String defPath, String defFileName, boolean overwrite) {
        this.configAPI = configAPI;
        this.plugin = configAPI.getPlugin();
        this.defPath = defPath;
        this.defFileName = defFileName;
        this.configFile = new File(path, filename + ".json");
        if (!configFile.exists() || overwrite) {
            configFile.getParentFile().mkdirs();
            loadDefaults(overwrite);
            onFirstInit();
        }
        load();
    }

    public JsonConfig(ConfigAPI configAPI, String path, String filename, String defPath, boolean overwrite){
        this(configAPI, path, filename, defPath, filename, overwrite);
    }

    public JsonConfig(ConfigAPI configAPI, String path, String fileName){
        this(configAPI, path, fileName, "me/wolfyscript/utilities/api/config/defaults","defJson", false);
    }

    /*
        Creates a jsonConfig with a jsonString as the default instead of a File. Saved in a File (path & filename)
        jsonString - the default json value.
        path - the config file path without ".json" at the end!
        overwrite - set if the existing file should be replaced by the defaults
     */
    public JsonConfig(ConfigAPI configAPI, String jsonString, String path, boolean overwrite){
        this.configAPI = configAPI;
        this.configFile = new File(path + ".json");
        if (!configFile.exists() || overwrite) {
            loadDefaults(overwrite);
            loadFromString(jsonString);
            save();
            onFirstInit();
        }
    }

    /*
        Creates a memory only json config!
     */
    public JsonConfig(ConfigAPI configAPI, String jsonString){
        this.configAPI = configAPI;
        loadFromString(jsonString);
    }

    /*
        Called when the config file didn't exist or whenever the config gets overwritten!
     */
    public void onFirstInit() {

    }

    public void init() {

    }

    public void loadDefaults(boolean overwrite) {
        if(defPath != null && defFileName != null){
            if (plugin.getResource(defPath + "/" + defFileName + ".json") != null) {
                plugin.saveResource(defPath + "/" + defFileName + ".json", overwrite);
            }
        }
    }

    public void loadDefaults(){
        loadDefaults(false);
    }

    public void loadFromString(String json){
        map = gson.fromJson(json, new HashMap<String, Object>().getClass());
    }

    public String toString(boolean prettyPrinting) {
        Gson gsonBuilder = prettyPrinting ? gson : new GsonBuilder().create();
        return gsonBuilder.toJson(map);
    }

    public String toString() {
        return toString(false);
    }

    public void load() {
        if(linkedToFile()){
            try {
                map = gson.fromJson(new FileReader(this.configFile), new HashMap<String, Object>().getClass());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(boolean prettyPrinting) {
        if(linkedToFile()){
            final String json = toString(prettyPrinting);
            configFile.delete();
            try {
                Files.write(configFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        save(false);
    }

    public void reload() {
        save();
        load();
    }

    public void linkToFile(String path){
        this.configFile = new File(path+".json");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            loadDefaults();
            onFirstInit();
        }
    }

    public void linkToFile(String path, String fileName){
        linkToFile(path+"/"+fileName);
    }

    public boolean linkedToFile(){
        return configFile != null;
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
