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

package com.wolfyscript.utilities.compatibility.plugins.itemsadder;

import com.google.inject.Inject;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ItemsAdderIntegration;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import java.util.Optional;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.CustomBlock;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

@WUPluginIntegration(pluginName = ItemsAdderIntegration.KEY)
public class ItemsAdderImpl extends PluginIntegrationAbstract implements ItemsAdderIntegration, Listener {

    @Inject
    protected ItemsAdderImpl(WolfyCoreSpigot core) {
        super(core, ItemsAdderIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.getRegistries().getStackIdentifierParsers().register(new ItemsAdderStackIdentifier.Parser());
        Bukkit.getPluginManager().registerEvents(this, core.getWolfyUtils().getPlugin());
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(this), core.getWolfyUtils().getPlugin());
    }

    @Override
    public boolean hasAsyncLoading() {
        return true;
    }

    @EventHandler
    public void onLoaded(ItemsAdderLoadDataEvent event) {
        if (event.getCause().equals(ItemsAdderLoadDataEvent.Cause.FIRST_LOAD)) {
            markAsDoneLoading();
        }
    }

    @Override
    public Optional<CustomStack> getStackByItemStack(ItemStack itemStack) {
        return CustomStackWrapper.wrapStack(dev.lone.itemsadder.api.CustomStack.byItemStack(itemStack));
    }

    @Override
    public Optional<CustomStack> getStackInstance(String namespacedID) {
        return CustomStackWrapper.wrapStack(dev.lone.itemsadder.api.CustomStack.getInstance(namespacedID));
    }

    @Override
    public @Nullable Optional<CustomBlock> getBlockByItemStack(ItemStack itemStack) {
        return CustomBlockWrapper.wrapBlock(dev.lone.itemsadder.api.CustomBlock.byItemStack(itemStack));
    }

    @Override
    public @Nullable Optional<CustomBlock> getBlockPlaced(Block block) {
        return CustomBlockWrapper.wrapBlock(dev.lone.itemsadder.api.CustomBlock.byAlreadyPlaced(block));
    }

    @Override
    public @Nullable Optional<CustomBlock> getBlockInstance(String namespacedID) {
        return CustomBlockWrapper.wrapBlock(dev.lone.itemsadder.api.CustomBlock.getInstance(namespacedID));
    }
}
