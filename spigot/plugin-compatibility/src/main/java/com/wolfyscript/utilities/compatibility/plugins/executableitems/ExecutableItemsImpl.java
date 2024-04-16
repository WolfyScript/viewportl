package com.wolfyscript.utilities.compatibility.plugins.executableitems;

import com.google.inject.Inject;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ExecutableItemsIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import org.bukkit.plugin.Plugin;

import java.util.List;

@WUPluginIntegration(pluginName = ExecutableItemsIntegration.PLUGIN_NAME)
public class ExecutableItemsImpl extends PluginIntegrationAbstract implements ExecutableItemsIntegration {

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    @Inject
    protected ExecutableItemsImpl(WolfyCoreSpigot core) {
        super(core, ExecutableItemsIntegration.PLUGIN_NAME);
    }

    @Override
    public void init(Plugin plugin) {
        core.getRegistries().getStackIdentifierParsers().register(new ExecutableItemsStackIdentifier.Parser(ExecutableItemsAPI.getExecutableItemsManager()));
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isValidID(String id) {
        return ExecutableItemsAPI.getExecutableItemsManager().isValidID(id);
    }

    @Override
    public List<String> getExecutableItemIdsList() {
        return ExecutableItemsAPI.getExecutableItemsManager().getExecutableItemIdsList();
    }
}
