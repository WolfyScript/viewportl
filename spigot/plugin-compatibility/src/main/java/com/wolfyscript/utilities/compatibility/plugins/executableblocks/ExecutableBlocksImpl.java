package com.wolfyscript.utilities.compatibility.plugins.executableblocks;

import com.ssomar.executableblocks.executableblocks.ExecutableBlocksManager;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import java.util.List;
import java.util.Optional;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ExecutableBlocksIntegration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = ExecutableBlocksIntegration.PLUGIN_NAME)
public class ExecutableBlocksImpl extends PluginIntegrationAbstract implements ExecutableBlocksIntegration {

    private ExecutableBlocksManager manager;

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *  @param core The WolfyUtilCore.
     *
     * @param pluginName The name of the associated plugin.
     */
    protected ExecutableBlocksImpl(WolfyCoreSpigot core, String pluginName) {
        super(core, pluginName);
    }

    @Override
    public void init(Plugin plugin) {
        this.manager = ExecutableBlocksManager.getInstance();
        core.getRegistries().getStackIdentifierParsers().register(new ExecutableBlocksStackIdentifier.Parser(this, manager));
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isValidID(String id) {
        return manager.isValidID(id);
    }

    @Override
    public List<String> getExecutableBlockIdsList() {
        return manager.getLoadedObjectsIDs(); // Older version of EB uses a different method name. Guess the API changed
    }

    @Override
    public Optional<String> getExecutableBlock(ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null || stack.getItemMeta().getPersistentDataContainer().isEmpty()) return Optional.empty();
        return Optional.ofNullable(stack.getItemMeta().getPersistentDataContainer().get(BLOCK_ID, PersistentDataType.STRING)).map(s -> manager.isValidID(s) ? s : null);
    }
}
