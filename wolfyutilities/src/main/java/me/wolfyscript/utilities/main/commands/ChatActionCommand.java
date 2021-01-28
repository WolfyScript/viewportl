package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.PlayerAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ChatActionCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        System.out.println("sender: " + sender.getName());
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length > 0) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args[0]);
            } catch (IllegalArgumentException expected) {
                return true;
            }
            PlayerAction action = Chat.CLICK_DATA_MAP.get(uuid);
            if (action != null && player.getUniqueId().equals(action.getUuid())) {
                action.run(player);
                if (action.isDiscard()) {
                    Chat.CLICK_DATA_MAP.remove(uuid);
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
