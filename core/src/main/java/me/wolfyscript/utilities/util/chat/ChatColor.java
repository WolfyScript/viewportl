package me.wolfyscript.utilities.util.chat;

import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class ChatColor {

    private ChatColor() {
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&#\\([A-Fa-f0-9]{6}\\)");

    public static String convert(@Nullable String msg) {
        if (msg == null) return null;
        char[] b = parseHexColorsString(msg).toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&') {
                if (b[i + 1] == '&') {
                    b[i + 1] = '=';
                } else if (b[i + 1] != ' ') {
                    b[i] = org.bukkit.ChatColor.COLOR_CHAR;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }
        }
        return new String(b).replace("&=", "&");
    }

    public static String parseHexColorsString(@Nullable String string) {
        if (string == null) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_16)) {
            var matcher = HEX_PATTERN.matcher(string);
            while (matcher.find()) {
                String group = matcher.group(0);
                if (string.contains(group)) {
                    string = string.replace(group, net.md_5.bungee.api.ChatColor.of(group.replace("(", "").replace(")", "").replace("&", "")).toString());
                }
            }
        }
        return string;
    }


}
