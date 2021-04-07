package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class InputCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            WolfyUtilities.getAPIList().parallelStream()
                    .filter(WolfyUtilities::hasInventoryAPI)
                    .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                    .filter(GuiHandler::isChatEventActive)
                    .forEach(guiHandler -> Bukkit.getScheduler().runTask(WolfyUtilities.getWUPlugin(), () -> {
                        //Handles ChatInput
                        if (!guiHandler.onChat(player, String.join(" ", args).trim(), args)) {
                            guiHandler.setChatInputAction(null);
                            guiHandler.openCluster();
                        }
                        if (guiHandler.isChatEventActive()) {
                            guiHandler.cancelChatInput();
                        }
                    }));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return WolfyUtilities.getAPIList().stream()
                    .filter(WolfyUtilities::hasInventoryAPI)
                    .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                    .filter(guiHandler -> guiHandler.isChatEventActive() && guiHandler.hasChatTabComplete())
                    .map(guiHandler -> guiHandler.getChatTabComplete().onTabComplete(guiHandler, player, args)).filter(Objects::nonNull).findFirst().orElse(null);
        }
        return null;
    }
}
