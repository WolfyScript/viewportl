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
import com.wolfyscript.utilities.bukkit.nbt.NBTQuery;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class QueryDebugCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreCommon core;

    public QueryDebugCommand(WolfyCoreCommon core) {
        super("query_item");
        this.core = core;
        setUsage("/query_item");
        setPermission("wolfyutilities.command.query_debug");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player) || !testPermission(sender)) return true;
        ItemStack stack = player.getEquipment().getItem(EquipmentSlot.HAND);
        if (!ItemUtils.isAirOrNull(stack)) {
            File file = new File(core.getWolfyUtils().getDataFolder(), "query_debug.json");
            if (file.exists()) {
                NBTQuery.of(file).ifPresent(nbtQuery -> {
                    NBTItem nbtItem = new NBTItem(stack);
                    NBTCompound result = nbtQuery.run(nbtItem);

                    System.out.println(result.toString());
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("true")) {
                            ItemStack stackToMergeIn = player.getEquipment().getItem(EquipmentSlot.OFF_HAND);
                            NBTItem nbtItem1 = new NBTItem(stackToMergeIn);
                            nbtItem1.mergeCompound(result);
                            nbtItem1.applyNBT(stackToMergeIn);
                        }
                    }
                });
            }
        }
        return true;
    }

}
