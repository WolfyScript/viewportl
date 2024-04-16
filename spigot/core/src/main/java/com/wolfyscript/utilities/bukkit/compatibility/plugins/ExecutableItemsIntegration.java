package com.wolfyscript.utilities.bukkit.compatibility.plugins;

import java.util.List;

public interface ExecutableItemsIntegration {

    String PLUGIN_NAME = "ExecutableItems";


    /**
     * Verify if id is a valid ExecutableItem ID
     *
     * @param id The ID to verify
     * @return true if it is a valid ID, false otherwise
     **/
    boolean isValidID(String id);

    /**
     * Get all ExecutableItems Ids
     *
     * @return All ExecutableItems ids
     **/
    List<String> getExecutableItemIdsList();
}
