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

package me.wolfyscript.utilities.compatibility.plugins;

import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.EcoIntegration;
import me.wolfyscript.utilities.compatibility.plugins.eco.EcoRefImpl;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

@WUPluginIntegration(pluginName = EcoIntegration.KEY)
public class EcoIntegrationImpl extends PluginIntegrationAbstract implements EcoIntegration {

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    protected EcoIntegrationImpl(WolfyUtilCore core) {
        super(core, EcoIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new EcoRefImpl.Parser());
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return true;
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isCustomItem(ItemStack itemStack) {
        return Items.isCustomItem(itemStack);
    }

    @Override
    @Nullable
    public NamespacedKey getCustomItem(ItemStack itemStack) {
        CustomItem customItem = Items.getCustomItem(itemStack);
        return customItem != null ? customItem.getKey() : null;
    }

    @Override
    public ItemStack lookupItem(String key) {
        return Items.lookup(key).getItem();
    }


}
