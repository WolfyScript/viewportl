package com.wolfyscript.utilities.compatibility.plugins.denizen;

import com.google.inject.Inject;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = DenizenIntegrationImpl.PLUGIN_NAME)
public class DenizenIntegrationImpl extends PluginIntegrationAbstract {

    public static final String PLUGIN_NAME = "Denizen";

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    @Inject
    protected DenizenIntegrationImpl(WolfyCoreSpigot core) {
        super(core, PLUGIN_NAME);
    }

    @Override
    public void init(Plugin plugin) {
        core.getRegistries().getStackIdentifierParsers().register(new DenizenStackIdentifier.Parser());
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

}
