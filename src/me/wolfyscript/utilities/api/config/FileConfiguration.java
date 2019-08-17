package me.wolfyscript.utilities.api.config;

import java.io.File;

public abstract class FileConfiguration extends MemoryConfiguration {

    protected File configFile;
    protected String defPath;
    protected String defFileName;

    protected boolean saveAfterValueSet = false;

    public FileConfiguration(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, Type type) {
        super(configAPI, name, type);
        this.defPath = defPath;
        this.defFileName = defFileName;
        if(!path.isEmpty() && !name.isEmpty()){
            this.configFile = new File(path, name + "." + (type.equals(Type.YAML) ? "yml" : "json"));
        }
    }

    /*
        Saves the config
     */
    abstract public void save();

    /*
        Loads the config out of the File!
        If changes were not saved then they will be overwritten!
     */
    abstract public void load();

    abstract public void reload();

    abstract public void loadDefaults();

    abstract public void onFirstInit();

    abstract public void init();


    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public boolean isSaveAfterValueSet() {
        return saveAfterValueSet;
    }

    /*
        Set if the config should be saved and reloaded after a new value was set.
        Could cause some trouble with some Configs like Json.
     */
    public void setSaveAfterValueSet(boolean saveAfterValueSet) {
        this.saveAfterValueSet = saveAfterValueSet;
    }
}
