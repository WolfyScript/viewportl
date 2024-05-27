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
import com.wolfyscript.utilities.gui.Window;
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
                .map(runtime -> new Pair<>(runtime, runtime.getCurrentMenu()))
                .filter(pair -> pair.getValue() != null && pair.getValue().getOnTextInput() != null)
                .forEach(pair -> {
                    ViewRuntime runtime = pair.getKey();
                    Window window = pair.getValue();
                    String text = String.join(" ", args).trim();

                    Bukkit.getScheduler().runTask(core.plugin, () -> {
                        window.getOnTextInput().run(BukkitWrapper.adapt(player), null, text, args);
                        window.setOnTextInput(null);
                        window.setOnTextInputTabComplete(null);
                        runtime.open();
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
                    .map(viewManager -> new Pair<>(viewManager, viewManager.getCurrentMenu()))
                    .filter(pair -> pair.getValue() != null && pair.getValue().getOnTextInputTabComplete() != null)
                    .findFirst()
                    .map(pair -> {
                        ViewRuntime runtime = pair.getKey();
                        Window window = pair.getValue();
                        return  window.getOnTextInputTabComplete().apply(BukkitWrapper.adapt(player), runtime, String.join(" ", args).trim(), args);
                    }).orElse(List.of());
        }
        return super.tabComplete(sender, alias, args);
    }
}
