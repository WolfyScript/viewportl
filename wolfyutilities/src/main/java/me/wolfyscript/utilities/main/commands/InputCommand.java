package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class InputCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) return true;
        if (args.length > 1) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);
            if (plugin != null) {
                WolfyUtilities wolfyUtilities = WolfyUtilities.get(plugin);
                if (wolfyUtilities != null && wolfyUtilities.hasInventoryAPI()) {
                    InventoryAPI<?> invAPI = wolfyUtilities.getInventoryAPI();
                    Player player = Bukkit.getPlayer(UUID.fromString(args[1]));
                    if (player != null) {
                        GuiHandler<?> guiHandler = invAPI.getGuiHandler(player);
                        //Handles ChatInput
                        if (guiHandler.isChatEventActive()) {
                            String message = "";
                            if (args.length > 2) {
                                StringBuilder messageBuilder = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    messageBuilder.append(args[i]).append(" ");
                                }
                                message = messageBuilder.toString().trim();
                            }
                            if (!guiHandler.onChat(player, message, message.split(" "))) {
                                guiHandler.setChatInputAction(null);
                                guiHandler.openCluster();
                            }
                        }
                        if (guiHandler.isChatEventActive()) {
                            guiHandler.cancelChatEvent();
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
