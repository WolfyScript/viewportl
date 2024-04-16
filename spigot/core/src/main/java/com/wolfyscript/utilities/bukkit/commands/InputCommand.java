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

package com.wolfyscript.utilities.bukkit.commands;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.adapters.BukkitWrapper;
import com.wolfyscript.utilities.gui.ViewRuntime;
import com.wolfyscript.utilities.gui.callback.TextInputCallback;
import com.wolfyscript.utilities.gui.callback.TextInputTabCompleteCallback;
import com.wolfyscript.utilities.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class InputCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreCommon core;

    public InputCommand(WolfyCoreCommon core) {
        super("wui");
        this.core = core;
        setUsage("/wui <input>");
        setDescription("Input for chat input actions");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        core.getWolfyUtilsInstanceList().parallelStream()
                .map(WolfyUtils::getGuiManager)
                .flatMap(guiAPIManager -> guiAPIManager.getViewManagersFor(player.getUniqueId()))
                .map(viewManager -> new Pair<>(viewManager, viewManager.textInputCallback()))
                .filter(pair -> pair.getValue().isPresent())
                .forEach(pair -> {
                    ViewRuntime viewManager = pair.getKey();
                    TextInputCallback textInputCallback = pair.getValue().get();
                    String text = String.join(" ", args).trim();

                    Bukkit.getScheduler().runTask(core.plugin, () -> {
                        textInputCallback.run(BukkitWrapper.adapt(player), viewManager, text, args);
                        viewManager.setTextInputCallback(null);
                        viewManager.setTextInputTabCompleteCallback(null);
                        viewManager.open();
                    });
                });
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (sender instanceof Player player) {
            return core.getWolfyUtilsInstanceList().parallelStream()
                    .map(WolfyUtils::getGuiManager)
                    .flatMap(guiAPIManager -> guiAPIManager.getViewManagersFor(player.getUniqueId()))
                    .map(viewManager -> new Pair<>(viewManager, viewManager.textInputTabCompleteCallback()))
                    .filter(pair -> pair.getValue().isPresent())
                    .findFirst()
                    .map(pair -> {
                        ViewRuntime viewManager = pair.getKey();
                        TextInputTabCompleteCallback textInputCallback = pair.getValue().get();
                        return textInputCallback.apply(BukkitWrapper.adapt(player), viewManager, String.join(" ", args).trim(), args);
                    }).orElse(List.of());
        }
        return super.tabComplete(sender, alias, args);
    }
}
