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

package me.wolfyscript.utilities.util;

import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Reflection {

    /**
     * The server version string to location NMS & OBC classes
     */
    private static final String VERSION = getVersion();
    private static final String NMS;
    private static final String CRAFTBUKKIT = "org.bukkit.craftbukkit." + VERSION + ".";
    /**
     * Cache of NMS classes that we've searched for
     */
    private static final Map<String, Class<?>> loadedNMSClasses = new HashMap<>();
    /**
     * Cache of OBS classes that we've searched for
     */
    private static final Map<String, Class<?>> loadedOBCClasses = new HashMap<>();
    /**
     * Cache of methods that we've found in particular classes
     */
    private static final Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> loadedDeclaredMethods = new HashMap<>();
    /**
     * Cache of fields that we've found in particular classes
     */
    private static final Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();
    private static final Map<Class<?>, Map<String, Field>> loadedDeclaredFields = new HashMap<>();
    private static final Map<Class<?>, Map<Class<?>, Field>> foundFields = new HashMap<>();

    static {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_17)) {
            NMS = "net.minecraft.";
        } else {
            NMS = "net.minecraft.server." + VERSION + ".";
        }
    }

    private Reflection() {}

    /**
     * Gets the version from the version dependent CraftBukkit package. e.g. v1_17_R1
     *
     * @return The CraftBukkit version.
     */
    public static String getVersion() {
        if(VERSION != null){
            return VERSION;
        }
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    @Deprecated
    public static int getVersionNumber() {
        return Integer.parseInt(getVersion().replace("_", "").replace("v", "").replaceAll("R[0-9]", ""));
    }

    /**
     * Get a class from the org.bukkit.craftbukkit package
     *
     * @param obcClassName the path to the class
     * @return the found class at the specified path
     */
    public static synchronized Class<?> getOBC(String obcClassName) {
        if (loadedOBCClasses.containsKey(obcClassName)) {
            return loadedOBCClasses.get(obcClassName);
        }
        String clazzName = CRAFTBUKKIT + obcClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            loadedOBCClasses.put(obcClassName, null);
            return null;
        }
        loadedOBCClasses.put(obcClassName, clazz);
        return clazz;
    }

    /**
     * Get an NMS Class
     *
     * @param nmsClassName The name of the class
     * @return The class
     */
    public static Class<?> getNMS(String nmsClassName) {
        if (loadedNMSClasses.containsKey(nmsClassName)) {
            return loadedNMSClasses.get(nmsClassName);
        }
        String clazzName = NMS + nmsClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return loadedNMSClasses.put(nmsClassName, null);
        }
        loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static Class<?> getNMS(String mojangPkg, String className) {
        return getNMS((ServerVersion.isAfterOrEq(MinecraftVersions.v1_17) ? mojangPkg + "." : "") + className);
    }

    public static Class<?> getNMS(String mojangPkg, NMSMapping mapping) {
        return getNMS(mojangPkg, mapping.get());
    }

    /**
     * Get a Bukkit {@link Player} players NMS playerConnection object
     *
     * @param player The player
     * @return The players connection
     */
    public static Object getConnection(Player player) {
        var getHandleMethod = getMethod(getOBC("entity.CraftPlayer"), "getHandle");
        if (getHandleMethod != null) {
            try {
                Object nmsPlayer = getHandleMethod.invoke(player);
                Field playerConField = getField(nmsPlayer.getClass(), NMSMapping.of(MinecraftVersions.v1_17, "b").orElse("playerConnection"));
                return playerConField.get(nmsPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void sendPacket(@NotNull Player player, @NotNull Object packet) {
        try {
            Object connection = getConnection(player);
            var sendPacketMethod = getMethod(
                    getNMS("server.network", "PlayerConnection"), NMSMapping.of(MinecraftVersions.v1_18, "a").orElse("sendPacket"),
                    getNMS("network.protocol", "Packet")
            );
            // Checking if the connection is not null is enough. There is no need to check if the player is online.
            if (connection != null) {
                sendPacketMethod.invoke(connection, packet);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Get a classes constructor
     *
     * @param clazz  The constructor class
     * @param params The parameters in the constructor
     * @return The constructor object
     */
    public static Constructor<?> getConstructor(@NotNull Class<?> clazz, Class<?>... params) {
        try {
            return clazz.getConstructor(params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Get a method from a class that has the specific parameters
     *
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate parameters
     */
    public static Method getMethod(@NotNull Class<?> clazz, String methodName, Class<?>... params) {
        return getMethod(false, clazz, methodName, params);
    }

    /**
     * Get a method from a class that has the specific parameters
     *
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate parameters
     */
    public static Method getMethod(boolean silent, @NotNull Class<?> clazz, String methodName, Class<?>... params) {
        loadedMethods.computeIfAbsent(clazz, aClass -> new HashMap<>());
        Map<String, Method> methods = loadedMethods.get(clazz);
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        try {
            Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            loadedMethods.put(clazz, methods);
            return method;
        } catch (Exception e) {
            if (!silent) {
                e.printStackTrace();
            }
            methods.put(methodName, null);
            loadedMethods.put(clazz, methods);
            return null;
        }
    }

    /**
     * Get a declared method from a class that has the specific parameters
     *
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate parameters
     */
    public static Method getDeclaredMethod(@NotNull Class<?> clazz, String methodName, Class<?>... params) {
        return getDeclaredMethod(false, clazz, methodName, params);
    }

    /**
     * Get a declared method from a class that has the specific parameters
     *
     * @param silent     If set to true it won't print the stacktrace on failed attempt
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate parameters
     */
    public static Method getDeclaredMethod(boolean silent, @NotNull Class<?> clazz, String methodName, Class<?>... params) {
        loadedDeclaredMethods.computeIfAbsent(clazz, aClass -> new HashMap<>());
        Map<String, Method> methods = loadedDeclaredMethods.get(clazz);
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName, params);
            methods.put(methodName, method);
            loadedDeclaredMethods.put(clazz, methods);
            return method;
        } catch (Exception e) {
            if (!silent) {
                e.printStackTrace();
            }
            methods.put(methodName, null);
            loadedDeclaredMethods.put(clazz, methods);
            return null;
        }
    }

    /**
     * Get a field with a particular name from a class
     *
     * @param clazz     The class
     * @param fieldName The name of the field
     * @return The field object
     */
    public static Field getField(@NotNull Class<?> clazz, String fieldName) {
        loadedFields.computeIfAbsent(clazz, aClass -> new HashMap<>());
        Map<String, Field> fields = loadedFields.get(clazz);
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        try {
            Field field = clazz.getField(fieldName);
            fields.put(fieldName, field);
            loadedFields.put(clazz, fields);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            loadedFields.put(clazz, fields);
            return null;
        }
    }

    /**
     * Get a declared field with a particular name from a class
     *
     * @param clazz     The class
     * @param fieldName The name of the field
     * @return The field object
     */
    public static Field getDeclaredField(@NotNull Class<?> clazz, String fieldName) {
        loadedDeclaredFields.computeIfAbsent(clazz, aClass -> new HashMap<>());
        Map<String, Field> fields = loadedDeclaredFields.get(clazz);
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        try {
            Field field = clazz.getDeclaredField(fieldName);
            fields.put(fieldName, field);
            loadedDeclaredFields.put(clazz, fields);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            loadedDeclaredFields.put(clazz, fields);
            return null;
        }
    }

    /**
     * Gets the first Field with the correct return type
     *
     * @param clazz The class
     * @param type  The return type
     * @return The field object
     */
    public static Field findField(@NotNull Class<?> clazz, Class<?> type) {
        foundFields.computeIfAbsent(clazz, aClass -> new HashMap<>());
        Map<Class<?>, Field> fields = foundFields.get(clazz);
        if (fields.containsKey(type)) {
            return fields.get(type);
        }
        try {
            List<Field> allFields = new ArrayList<>();
            allFields.addAll(Arrays.asList(clazz.getFields()));
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            for(Field f : allFields){
                if(type.equals(f.getType())){
                    fields.put(type, f);
                    foundFields.put(clazz, fields);
                    return f;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fields.put(type, null);
            foundFields.put(clazz, fields);
        }
        return null;
    }

    public static final class NMSMapping {

        private final MinecraftVersion version;
        private final String mapping;

        private NMSMapping(MinecraftVersion version, String mapping) {
            this.version = version;
            this.mapping = mapping;
        }

        public static NMSMapping of(MinecraftVersion version, String mapping) {
            return new NMSMapping(version, mapping);
        }

        public NMSMapping or(NMSMapping nmsMapping) {
            return ServerVersion.getVersion().isAfterOrEq(nmsMapping.version) ? nmsMapping : this;
        }

        public String orElse(String mapping) {
            return ServerVersion.getVersion().isAfterOrEq(version) ? this.mapping : mapping;
        }

        public String get() {
            return mapping;
        }

    }

}
