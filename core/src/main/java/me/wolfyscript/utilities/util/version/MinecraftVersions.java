package me.wolfyscript.utilities.util.version;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersions {

    public static final MinecraftVersion v1_16 = MinecraftVersion.parse("1.16");
    public static final MinecraftVersion v1_15 = MinecraftVersion.parse("1.15");
    public static final MinecraftVersion v1_14 = MinecraftVersion.parse("1.14");
    public static final MinecraftVersion v1_13 = MinecraftVersion.parse("1.13");

    static final MinecraftVersion RUNTIME_VERSION;

    static {
        Matcher version = Pattern.compile("\\(MC: ([0-9].*)\\)").matcher(Bukkit.getVersion());
        if (version.find() && version.group(1) != null) {
            RUNTIME_VERSION = MinecraftVersion.parse(version.group(1));
        } else {
            throw new IllegalStateException("Cannot parse version String '" + Bukkit.getVersion() + "'");
        }
    }

}
