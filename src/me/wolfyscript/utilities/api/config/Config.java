package me.wolfyscript.utilities.api.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class Config {

    private File configFile;
    private YamlConfiguration config;
    protected ConfigAPI configAPI;
    private String name;
    private String defaultPath;
    private String defaultName;
    private Plugin plugin;
    private boolean saveAfterSet;
    private int intervalsToPass = 0;
    private int passedIntervals;
    private boolean firstInit = false;

    /*
        plugin - your plugin
        defaultPath - the path to the file inside the jar, which contains the default values!
        defaultName - the name of the default file!
        savePath - The path where the file will be save to!
        name - The name of the file and the name of the default file.
     */
    public Config(ConfigAPI configAPI, String defaultPath, String defaultName, String savePath, String name) {
        this.name = name;
        this.saveAfterSet = true;
        this.plugin = configAPI.getPlugin();
        this.defaultPath = defaultPath;
        this.defaultName = defaultName;
        this.configAPI = configAPI;
        configFile = new File(savePath, name + ".yml");
        if(!configFile.exists()){
            firstInit = true;
            try {
                configFile.getParentFile().mkdir();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        if(firstInit){
            loadDefaults();
            onFirstInit();
            firstInit = false;
        }
        init();
    }

    public Config(ConfigAPI configAPI, String defaultPath, String savePath, String name) {
        this(configAPI, defaultPath, name, savePath, name);
    }

    /*
        defaultPath is not used here!
        Use this if your default file is in the source folder and not in another package!
     */
    public Config(ConfigAPI configAPI, String savePath, String name) {
        this(configAPI, "", savePath, name);
    }

    /*
        savePath is not used here!
        Use this one if your file should be saved in the default Plugin directory!
     */
    public Config(ConfigAPI configAPI, String name) {
        this(configAPI, configAPI.getPlugin().getDataFolder().getPath(), name);
    }

    /*
        This method is called when the file does not exists.
        Can be overridden.
     */
    public void onFirstInit(){
    }

    /*
        This method is called every time the config is initiated
        Can be overridden.
     */
    public void init(){
    }

    public boolean isFirstInit() {
        return firstInit;
    }

    /*
        Auto-save intervals this Config has to pass to be saved.
    */
    public void setIntervalsToPass(int intervalsToPass) {
        this.intervalsToPass = intervalsToPass;
    }

    /*
        Set if the config should be saved and reloaded after a new value was set.
     */
    public void saveAfterSet(boolean enable){
        this.saveAfterSet = enable;
    }

    /*
        loads the defaults out of the path you set on init!
     */
    public void loadDefaults() {
        config.options().copyDefaults(true);
        Reader stream;
        try {
            String fileName = defaultName.isEmpty() ? name : defaultName;
            if(plugin.getResource(defaultPath.isEmpty() ? fileName : defaultPath +"/"+fileName+".yml") != null){
                stream = new InputStreamReader(plugin.getResource(defaultPath.isEmpty() ? fileName : defaultPath +"/"+fileName+".yml"), StandardCharsets.UTF_8);
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(stream);
                config.setDefaults(defConfig);
                stream.close();
            }
        } catch (IOException e) {
            //EMPTY
        }
        reload();
    }

    /*
        Saves and loads the config after it!
     */
    public void reload() {
        save();
        load();
    }

    void reloadAuto(){
        if(intervalsToPass > 0){
            if(passedIntervals < intervalsToPass){
                passedIntervals++;
            }else{
                reload();
            }
        }else{
            reload();
        }
    }

    /*
        Saves the config
     */
    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /*
        Sets a value to the path
     */
    public void set(String path, Object object){
        config.set(path, object);
        if(saveAfterSet){
            reload();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public Set<String> getKeys() {
        return config.getKeys(true);
    }

    public Object getObject(String path) {
        return config.get(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public List<String> getStringList(String path){
        return config.getStringList(path);
    }

    public String[] getStringArray(String path){
        return (String[]) getStringList(path).toArray();
    }




}
