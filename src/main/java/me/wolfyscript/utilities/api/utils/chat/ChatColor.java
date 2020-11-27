package me.wolfyscript.utilities.api.utils.chat;

import me.wolfyscript.utilities.api.WolfyUtilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColor {

    public static final String HEX_PATTERN = "&#\\([A-Fa-f0-9]{6}\\)";
    public static final String HEX_PATTERN_LOOKBEHIND = "(?<=&#\\([A-Fa-f0-9]{6}\\))";
    public static final Pattern HEX_PATTERN_COMPILED = Pattern.compile(HEX_PATTERN);
    public static final Pattern HEX_PATTERN_LOOKBEHIND_COMPILED = Pattern.compile(HEX_PATTERN_LOOKBEHIND);

    public static String convert(String msg) {
        char[] b = parseHexColorsString(msg).toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&') {
                if (b[i + 1] == '&') {
                    b[i + 1] = '=';
                } else {
                    b[i] = 167;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }
        }
        return new String(b).replace("&=", "&");
    }

    public static String parseHexColorsString(String string) {
        if (WolfyUtilities.hasNetherUpdate()) {
            String[] strings = HEX_PATTERN_LOOKBEHIND_COMPILED.split(string);
            StringBuilder sb = new StringBuilder();
            for (String selected : strings) {
                Matcher matcher = HEX_PATTERN_COMPILED.matcher(selected);
                if (matcher.find()) {
                    String snippet = matcher.group(0)
                            .replaceAll("[(]", "")
                            .replaceAll("[)]", "")
                            .replaceAll("&", "");
                    selected = selected.replaceAll(HEX_PATTERN, net.md_5.bungee.api.ChatColor.of(snippet) + "");
                }
                sb.append(selected);
            }
            return sb.toString();
        }
        return string;
    }


}
