package me.wolfyscript.utilities.api.config;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private WolfyUtilities api;

    /*
        plugin - your plugin
        defaultPath - the path to the file inside the jar, which contains the default values!
        defaultName - the name of the default file!
        savePath - The path where the file will be save to!
        name - The name of the file and the name of the default file.
        override - if true, the config will be overridden with the default values (onFirstInit() will run on every override!)
     */
    public Config(ConfigAPI configAPI, String defaultPath, String defaultName, String savePath, String name, boolean override) {
        this.api = configAPI.getApi();
        this.name = name;
        this.saveAfterSet = true;
        this.plugin = configAPI.getPlugin();
        this.defaultPath = defaultPath;
        this.defaultName = defaultName;
        this.configAPI = configAPI;
        configFile = new File(savePath, name + ".yml");
        if(override && configFile.exists()){
            if(!configFile.delete()){
                Main.getMainUtil().sendConsoleMessage("Error while trying to override Config!");
                Main.getMainUtil().sendConsoleMessage("File: "+configFile.getPath());
            }
        }
        if(!configFile.exists()){
            firstInit = true;
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                Main.getMainUtil().sendConsoleMessage("Error creating file: "+configFile.getPath());
                Main.getMainUtil().sendConsoleMessage("     cause: "+e.getMessage());
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


    /*
        plugin - your plugin
        defaultPath - the path to the file inside the jar, which contains the default values!
        defaultName - the name of the default file!
        savePath - The path where the file will be save to!
        name - The name of the file and the name of the default file.
     */
    public Config(ConfigAPI configAPI, String defaultPath, String defaultName, String savePath, String name) {
        this(configAPI, defaultPath, defaultName, savePath, name, false);
    }

    public Config(ConfigAPI configAPI, String defaultPath, String savePath, String name) {
        this(configAPI, defaultPath, name, savePath, name);
    }

    public Config(ConfigAPI configAPI, String defaultPath, String savePath, String name, boolean override) {
        this(configAPI, defaultPath, name, savePath, name, override);
    }

    /*
        defaultPath is not used here!
        Use this if your default file is in the source folder and not in another package!
     */
    public Config(ConfigAPI configAPI, String savePath, String name) {
        this(configAPI, "", savePath, name);
    }

    /*
        defaultPath is not used here!
        Use this if your default file is in the source folder and not in another package!
     */
    public Config(ConfigAPI configAPI, String savePath, String name, boolean override) {
        this(configAPI, "", savePath, name, override);
    }

    /*
        savePath is not used here!
        Use this one if your file should be saved in the default Plugin directory!
     */
    public Config(ConfigAPI configAPI, String name) {
        this(configAPI, configAPI.getPlugin().getDataFolder().getPath(), name);
    }

    /*
        savePath is not used here!
        Use this one if your file should be saved in the default Plugin directory!
     */
    public Config(ConfigAPI configAPI, String name, boolean override) {
        this(configAPI, configAPI.getPlugin().getDataFolder().getPath(), name, override);
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
            InputStream inputStream = plugin.getResource(defaultPath.isEmpty() ? fileName : defaultPath +"/"+fileName+".yml");
            if(inputStream != null){
                stream = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(stream);
                config.options().header(defConfig.options().header());
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

    public void saveItem(String path, ItemStack itemStack){
        if(itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.hasDisplayName()){
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace('§','&'));
            }
            if(itemMeta.hasLore()){
                List<String> newLore = new ArrayList<>();
                for(String row : itemMeta.getLore()){
                    newLore.add(row.replace('§','&'));
                }
                itemMeta.setLore(newLore);
            }
            itemStack.setItemMeta(itemMeta);
        }
        set(path, itemStack.serialize());
    }

    public void saveItem(String path, String name, ItemStack itemStack){
        saveItem(path+"."+name, itemStack);
    }

    public ItemStack getItem(String path){
        return getItem(path, true);
    }

    public ItemStack getItem(String path, boolean replaceKeys){
        if(getConfig().isSet(path)){
            Map<String, Object> data = getConfig().getConfigurationSection(path).getValues(false);
            data.put("v", Bukkit.getUnsafe().getDataVersion());
            ItemStack itemStack = ItemStack.deserialize(data);
            if(itemStack.hasItemMeta()){
                ItemMeta itemMeta = itemStack.getItemMeta();
                if(itemMeta.hasDisplayName()){
                    String displayName = itemMeta.getDisplayName();
                    if(replaceKeys && api.getLanguageAPI().getActiveLanguage() != null){
                        displayName = api.getLanguageAPI().getActiveLanguage().replaceKeys(displayName);
                    }
                    itemMeta.setDisplayName(WolfyUtilities.translateColorCodes(displayName));
                }
                if(itemMeta.hasLore()){
                    List<String> newLore = new ArrayList<>();
                    for(String row : itemMeta.getLore()){
                        if(replaceKeys && api.getLanguageAPI().getActiveLanguage() != null){
                            if(row.startsWith("[WU]")){
                                row = row.substring("[WU]".length());
                                row = api.getLanguageAPI().getActiveLanguage().replaceKeys(row);
                            }else if(row.startsWith("[WU!]")){
                                List<String> rows = api.getLanguageAPI().getActiveLanguage().replaceKey(row.substring("[WU!]".length()));
                                for(String newRow : rows){
                                    newLore.add(WolfyUtilities.translateColorCodes(newRow));
                                }
                                continue;
                            }
                        }
                        newLore.add(WolfyUtilities.translateColorCodes(row));
                    }
                    itemMeta.setLore(newLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }
        return new ItemStack(Material.STONE);
    }

    public File getConfigFile() {
        return configFile;
    }
}
