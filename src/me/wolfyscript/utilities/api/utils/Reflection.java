package me.wolfyscript.utilities.api.utils;

import me.wolfyscript.utilities.main.Main;

public class Reflection {

    public static String getServerVersion() {
        return Main.getInstance().getServer().getClass().getPackage().getName().substring(23);
    }

    public static Class<?> getOBC(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + '.' + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMS(String name) {
        try {
            return Class.forName("net.minecraft.server." + getServerVersion() + '.' + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
