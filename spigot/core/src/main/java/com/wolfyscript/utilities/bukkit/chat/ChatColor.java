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

package com.wolfyscript.utilities.bukkit.chat;

import com.wolfyscript.utilities.versioning.MinecraftVersions;
import com.wolfyscript.utilities.versioning.ServerVersion;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ChatColor {

    private ChatColor() {
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&#\\([A-Fa-f0-9]{6}\\)");
    private static final Pattern HEX_PATTERN_OTHER = Pattern.compile("&#[A-Fa-f0-9]{6}");

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
            matcher = HEX_PATTERN_OTHER.matcher(string);
            while (matcher.find()) {
                String group = matcher.group(0);
                if (string.contains(group)) {
                    string = string.replace(group, net.md_5.bungee.api.ChatColor.of(group.replace("&", "")).toString());
                }
            }
        }
        return string;
    }

}
