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

package com.wolfyscript.utilities.bukkit.config;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * This class is based on the bukkit yaml configuration.
 * It is subject to change in future updates, so make sure to frequently check for changes if you use it.
 */
public class YamlConfiguration extends org.bukkit.configuration.file.YamlConfiguration {

    private final String name;
    public WolfyUtilsBukkit api;
    public ConfigAPI configAPI;

    private int intervalsToPass = 0;
    private int passedIntervals;
    private boolean firstInit = false;
    public Plugin plugin;
    protected File configFile;
    protected String defPath;
    protected String defFileName;
    protected boolean saveAfterValueSet = false;

    /*
        plugin - your plugin
        defaultPath - the path to the file inside the jar, which contains the default values!
        defaultName - the name of the default file!
        savePath - The path where the file will be save to!
        name - The name of the file and the name of the default file.
        override - if true, the config will be overridden with the default values (onFirstInit() will run on every override!)
     */
    public YamlConfiguration(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, boolean override) {
        this.api = configAPI.getApi();
        this.configAPI = configAPI;
        this.plugin = configAPI.getPlugin();
        this.name = name;
        this.defPath = defPath;
        this.defFileName = defFileName;
        if (!path.isEmpty() && !name.isEmpty()) {
            this.configFile = new File(path, name + ".yml");
        }
        if (override && configFile.exists()) {
            if (!configFile.delete()) {
                Bukkit.getLogger().warning("Error while trying to override YamlConfiguration!");
                Bukkit.getLogger().warning("File: " + configFile.getPath());
            }
        }
        if (!configFile.exists()) {
            firstInit = true;

            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Error creating file: " + configFile.getPath());
                Bukkit.getLogger().warning("     cause: " + e.getMessage());
            }
        }
        try {
            load(configFile);
            if (firstInit) {
                loadDefaults();
                onFirstInit();
                firstInit = false;
            }
            init();
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().warning("Error loading config: " + configFile.getPath());
            Bukkit.getLogger().warning("     cause: " + ex.getMessage());
        }
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
        save();
        options().copyDefaults(true);
        Reader stream;
        try {
            String fileName = defFileName.isEmpty() ? getName() : defFileName;
            InputStream inputStream = plugin.getResource((defPath.isEmpty() ? fileName : defPath + "/" + fileName) + ".yml");
            if (inputStream != null) {
                stream = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                org.bukkit.configuration.file.YamlConfiguration defConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(stream);
                options().header(defConfig.options().header());
                setDefaults(defConfig);
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
            save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ItemStack getItem(String path) {
        return getItem(path, true);
    }

    @Nullable
    public ItemStack getItem(String path, boolean replaceKeys) {
        if (isSet(path)) {
            ItemStack itemStack = getItemStack(path);
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (replaceKeys && api.getTranslations().getActiveLanguage() != null) {
                        displayName = api.getTranslations().replaceKeys(displayName);
                    }
                    itemMeta.setDisplayName(ChatColor.convert(displayName));
                }
                if (itemMeta.hasLore()) {
                    List<String> newLore = new ArrayList<>();
                    for (String row : itemMeta.getLore()) {
                        if (replaceKeys && api.getTranslations().getActiveLanguage() != null) {
                            if (row.startsWith("[WU]")) {
                                row = row.substring("[WU]".length());
                                row = api.getTranslations().replaceKeys(row);
                            } else if (row.startsWith("[WU!]")) {
                                List<String> rows = api.getTranslations().replaceKey(row.substring("[WU!]".length()));
                                for (String newRow : rows) {
                                    newLore.add(ChatColor.convert(newRow));
                                }
                                continue;
                            }
                        }
                        newLore.add(ChatColor.convert(row));
                    }
                    itemMeta.setLore(newLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }
        return null;
    }
}
