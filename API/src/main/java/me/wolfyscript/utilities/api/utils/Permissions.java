package me.wolfyscript.utilities.api.utils;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.CommandSender;

public class Permissions {

    private final WolfyUtilities wolfyUtilities;

    public Permissions(WolfyUtilities wolfyUtilities){
        this.wolfyUtilities = wolfyUtilities;
    }

    public boolean hasPermission(CommandSender sender, String permCode) {
        if (sender.hasPermission("*")) return true;
        StringBuilder permission = new StringBuilder();
        for (String s : permCode.split("\\.")) {
            permission.append(s);
            if (sender.hasPermission(permission.toString()) || sender.hasPermission(permission.toString() + ".*")) {
                return true;
            }
            permission.append(".");
        }
        return false;
    }
}
