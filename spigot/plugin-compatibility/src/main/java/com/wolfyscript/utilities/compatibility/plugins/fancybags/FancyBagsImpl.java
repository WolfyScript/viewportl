package com.wolfyscript.utilities.compatibility.plugins.fancybags;

import com.google.inject.Inject;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = FancyBagsImpl.KEY)
public class FancyBagsImpl extends PluginIntegrationAbstract {

    public static final String KEY = "FancyBags";

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    @Inject
    protected FancyBagsImpl(WolfyCoreSpigot core) {
        super(core, KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.getRegistries().getStackIdentifierParsers().register(new com.wolfyscript.utilities.compatibility.plugins.fancybags.FancyBagsStackIdentifier.Parser());
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }
}
