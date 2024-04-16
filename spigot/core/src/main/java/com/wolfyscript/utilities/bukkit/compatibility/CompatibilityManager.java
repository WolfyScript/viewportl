package com.wolfyscript.utilities.bukkit.compatibility;

public interface CompatibilityManager {

    void init();

    boolean has1_20Features();

    Plugins getPlugins();

}
