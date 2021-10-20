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

package me.wolfyscript.utilities.util.inventory;

import me.wolfyscript.utilities.util.chat.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.Locale;

public class PotionUtils {

    private static final DecimalFormat timeFormat = new DecimalFormat("00");

    public static String getPotionName(PotionEffectType type) {
        var potionType = PotionType.getByEffect(type);
        return StringUtils.capitalize((potionType != null ? potionType.name() : type.getName()).toLowerCase(Locale.ENGLISH).replace("_", ""));
    }

    public static String getPotionEffectLore(int amplifier, int duration, PotionEffectType type) {
        int seconds = duration / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return ChatColor.convert("&9" + getPotionName(type) + " " + amplifier + String.format(" (%s:%s)", timeFormat.format(minutes), timeFormat.format(seconds)));
    }
}
