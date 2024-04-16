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
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import java.util.UUID;
import com.wolfyscript.utilities.bukkit.chat.PlayerAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ChatActionCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreCommon core;

    public ChatActionCommand(WolfyCoreCommon core) {
        super("wua");
        this.core = core;
        setDescription("Used to handle code execution on chat click events.");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length > 0) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args[0]);
            } catch (IllegalArgumentException expected) {
                return true;
            }
            PlayerAction action = BukkitChat.getClickData(uuid);
            if (action != null && player.getUniqueId().equals(action.getUuid())) {
                action.run(BukkitWrapper.adapt(player));
                if (action.isDiscard()) {
                    BukkitChat.removeClickData(uuid);
                }
            }
        }
        return true;
    }
}
