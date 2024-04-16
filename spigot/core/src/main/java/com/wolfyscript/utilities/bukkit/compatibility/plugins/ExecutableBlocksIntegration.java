package com.wolfyscript.utilities.bukkit.compatibility.plugins;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface ExecutableBlocksIntegration {

    String PLUGIN_NAME = "ExecutableBlocks";
    NamespacedKey BLOCK_ID = new NamespacedKey(PLUGIN_NAME.toLowerCase(Locale.ROOT), "eb-id");

    /**
     * Verify if id is a valid ExecutableBlock ID
     *
     * @param id The ID to verify
     * @return true if it is a valid ID, false otherwise
     **/
    boolean isValidID(String id);

    /**
     * Get all ExecutableBlock Ids
     *
     * @return All ExecutableBlock ids
     **/
    List<String> getExecutableBlockIdsList();

    /**
     * Gets the ExecutableBlock id that belongs to the specified ItemStack.
     *
     * @param stack The ItemStack that belongs to an ExecutableBlock
     * @return The id of the ExecutableBlock or an empty Optional.
     */
    Optional<String> getExecutableBlock(ItemStack stack);
}
