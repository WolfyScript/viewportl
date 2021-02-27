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
        PotionType potionType = PotionType.getByEffect(type);
        return StringUtils.capitalize((potionType != null ? potionType.name() : type.getName()).toLowerCase(Locale.ENGLISH).replace("_", ""));
    }

    public static String getPotionEffectLore(int amplifier, int duration, PotionEffectType type) {
        int seconds = duration / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return ChatColor.convert("&9" + getPotionName(type) + " " + amplifier + String.format(" (%s:%s)", timeFormat.format(minutes), timeFormat.format(seconds)));
    }
}
