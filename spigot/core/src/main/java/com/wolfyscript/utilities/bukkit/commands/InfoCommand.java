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

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.adapters.BukkitWrapper;
import com.wolfyscript.utilities.versioning.ServerVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class InfoCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreCommon core;

    public InfoCommand(WolfyCoreCommon core) {
        super("wolfyutils");
        this.core = core;
        setDescription("Displays info about the plugin version, etc.");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player bukkitPlayer)) return true;
        var player = BukkitWrapper.adapt(bukkitPlayer);
        core.getChat().sendMessages(player, true,
                Component.text("——————— ", NamedTextColor.GRAY).append(Component.text("WolfyUtilities", NamedTextColor.AQUA, TextDecoration.BOLD)).append(Component.text(" ———————")),
                Component.empty(),
                Component.text("Author: ", NamedTextColor.GRAY).append(Component.text(String.join(", ", core.getWolfyUtils().getPlugin().getDescription().getAuthors()), null, TextDecoration.BOLD)),
                Component.empty(),
                Component.text("Version: ", NamedTextColor.GRAY).append(Component.text(ServerVersion.getWUVersion().getVersion(), null, TextDecoration.BOLD)),
                Component.text("———————————————————————", NamedTextColor.GRAY)
        );
        return true;
    }

}
