package me.wolfyscript.utilities.api.config;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YamlConfiguration extends me.wolfyscript.utilities.api.config.FileConfiguration {

    private org.bukkit.configuration.file.YamlConfiguration config;
    private int intervalsToPass = 0;
    private int passedIntervals;
    private boolean firstInit = false;

    /*
        plugin - your plugin
        defaultPath - the path to the file inside the jar, which contains the default values!
        defaultName - the name of the default file!
        savePath - The path where the file will be save to!
        name - The name of the file and the name of the default file.
        override - if true, the config will be overridden with the default values (onFirstInit() will run on every override!)
     */
    public YamlConfiguration(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, boolean override) {
        super(configAPI, path, name, defPath, defFileName, Type.YAML);
        if (override && configFile.exists()) {
            if (!configFile.delete()) {
                Main.getMainUtil().sendConsoleMessage("Error while trying to override YamlConfiguration!");
                Main.getMainUtil().sendConsoleMessage("File: " + configFile.getPath());
            }
        }
        if (!configFile.exists()) {
            firstInit = true;
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                Main.getMainUtil().sendConsoleMessage("Error creating file: " + configFile.getPath());
                Main.getMainUtil().sendConsoleMessage("     cause: " + e.getMessage());
            }
        }
        config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);
        if (firstInit) {
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
    public YamlConfiguration(ConfigAPI configAPI, String defaultPath, String defaultName, String savePath, String name) {
        this(configAPI, savePath, name, defaultPath, defaultName, false);
    }

    public YamlConfiguration(ConfigAPI configAPI, String defaultPath, String savePath, String name) {
        this(configAPI, defaultPath, name, savePath, name);
    }

    public YamlConfiguration(ConfigAPI configAPI, String defaultPath, String savePath, String name, boolean override) {
        this(configAPI, defaultPath, name, savePath, name, override);
    }

    /*
        defaultPath is not used here!
        Use this if your default file is in the source folder and not in another package!
     */
    public YamlConfiguration(ConfigAPI configAPI, String savePath, String name) {
        this(configAPI, "", savePath, name);
    }

    /*
        defaultPath is not used here!
        Use this if your default file is in the source folder and not in another package!
     */
    public YamlConfiguration(ConfigAPI configAPI, String savePath, String name, boolean override) {
        this(configAPI, "", savePath, name, override);
    }

    /*
        savePath is not used here!
        Use this one if your file should be saved in the default Plugin directory!
     */
    public YamlConfiguration(ConfigAPI configAPI, String name) {
        this(configAPI, configAPI.getPlugin().getDataFolder().getPath(), name);
    }

    /*
        savePath is not used here!
        Use this one if your file should be saved in the default Plugin directory!
     */
    public YamlConfiguration(ConfigAPI configAPI, String name, boolean override) {
        this(configAPI, configAPI.getPlugin().getDataFolder().getPath(), name, override);
    }

    /*
        This method is called when the file does not exists.
        Can be overridden.
     */
    public void onFirstInit() {
    }

    /*
        This method is called every time the config is initiated
        Can be overridden.
     */
    public void init() {
    }

    public boolean isFirstInit() {
        return firstInit;
    }

    /*
        Auto-save intervals this YamlConfiguration has to pass to be saved.
    */
    public void setIntervalsToPass(int intervalsToPass) {
        this.intervalsToPass = intervalsToPass;
    }

    /*
        loads the defaults out of the path you set on init!
     */
    public void loadDefaults() {
        config.options().copyDefaults(true);
        Reader stream;
        try {
            String fileName = defFileName.isEmpty() ? getName() : defFileName;
            InputStream inputStream = plugin.getResource(defPath.isEmpty() ? fileName : defPath + "/" + fileName + ".yml");
            if (inputStream != null) {
                stream = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                org.bukkit.configuration.file.YamlConfiguration defConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(stream);
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

    void reloadAuto() {
        if (intervalsToPass > 0) {
            if (passedIntervals < intervalsToPass) {
                passedIntervals++;
            } else {
                reload();
            }
        } else {
            reload();
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String path, Object object) {
        config.set(path, object);
        if (saveAfterValueSet) {
            reload();
        }
    }

    @Override
    public Object get(String path) {
        return config.get(path);
    }

    @Override
    public Object get(String path, Object def) {
        return config.get(path, def);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public Type getType() {
        return Type.YAML;
    }

    public Set<String> getKeys() {
        return getKeys(false);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    @Override
    public Map<String, Object> getMap() {
        return config.getValues(false);
    }

    @Override
    public boolean hasPathSeparator() {
        return config.options().pathSeparator() != 0;
    }

    @Override
    public void setPathSeparator(char pathSeparator) {
        config.options().pathSeparator(pathSeparator);
    }

    @Override
    public char getPathSeparator() {
        return config.options().pathSeparator();
    }

    public Object getObject(String path) {
        return config.get(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    @Override
    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    @Override
    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    @Override
    public List<?> getList(String path) {
        return config.getList(path);
    }

    @Nonnull
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Deprecated
    @Override
    public void saveItem(String path, String name, ItemStack itemStack) {
        setItem(path, name, itemStack);
    }

    @Deprecated
    @Override
    public void saveItem(String path, ItemStack item) {
        setItem(path, item);
    }

    @Override
    public void setItem(String path, ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace('§', '&'));
            }
            if (itemMeta.hasLore()) {
                List<String> newLore = new ArrayList<>();
                for (String row : itemMeta.getLore()) {
                    newLore.add(row.replace('§', '&'));
                }
                itemMeta.setLore(newLore);
            }
            itemStack.setItemMeta(itemMeta);
        }
        set(path, itemStack.serialize());
    }

    @Override
    public void setItem(String path, String name, ItemStack itemStack) {
        setItem(path + "." + name, itemStack);
    }

    @Override
    public ItemStack getItem(String path) {
        return getItem(path, true);
    }

    @Nullable
    @Override
    public ItemStack getItem(String path, boolean replaceKeys) {
        if (config.isSet(path)) {
            Map<String, Object> data = getValues(path);
            data.put("v", Bukkit.getUnsafe().getDataVersion());
            ItemStack itemStack = ItemStack.deserialize(data);
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (replaceKeys && api.getLanguageAPI().getActiveLanguage() != null) {
                        displayName = api.getLanguageAPI().getActiveLanguage().replaceKeys(displayName);
                    }
                    itemMeta.setDisplayName(WolfyUtilities.translateColorCodes(displayName));
                }
                if (itemMeta.hasLore()) {
                    List<String> newLore = new ArrayList<>();
                    for (String row : itemMeta.getLore()) {
                        if (replaceKeys && api.getLanguageAPI().getActiveLanguage() != null) {
                            if (row.startsWith("[WU]")) {
                                row = row.substring("[WU]".length());
                                row = api.getLanguageAPI().getActiveLanguage().replaceKeys(row);
                            } else if (row.startsWith("[WU!]")) {
                                List<String> rows = api.getLanguageAPI().getActiveLanguage().replaceKey(row.substring("[WU!]".length()));
                                for (String newRow : rows) {
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
        return null;
    }

    @Override
    public Map<String, Object> getValues(String path) {
        if (config.getConfigurationSection(path) != null) {
            return config.getConfigurationSection(path).getValues(false);
        }
        return new HashMap<>();
    }

    public org.bukkit.configuration.file.YamlConfiguration getBukkitConfig(){
        return config;
    }
}
