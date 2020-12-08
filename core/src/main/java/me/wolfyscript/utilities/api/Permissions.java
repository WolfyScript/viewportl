package me.wolfyscript.utilities.api;

import org.bukkit.command.CommandSender;

import java.util.Locale;

public class Permissions {

    private final WolfyUtilities wolfyUtilities;
    private String permissionKey;

    public Permissions(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.permissionKey = wolfyUtilities.getPlugin().getName().toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    /**
     * This permission key is the first key of the permission.<br/>
     * For example <b><i>wolfyutilities.*<i/><b/>
     *
     * @return the first permission key
     */
    public String getPermissionKey() {
        return permissionKey;
    }

    /**
     * @param permissionKey
     */
    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
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
