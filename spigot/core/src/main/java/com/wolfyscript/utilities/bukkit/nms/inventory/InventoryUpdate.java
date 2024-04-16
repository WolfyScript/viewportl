/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Matsubara
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.wolfyscript.utilities.bukkit.nms.inventory;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.nms.Reflection;
import com.wolfyscript.utilities.versioning.ServerVersion;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A utility class to update the inventory of a player.
 * This is useful to change the title of an inventory.
 *
 * @deprecated Spigot introduced the InventoryView#setTitle(String) method in 1.20 rendering this redundant.
 */
@Deprecated
public final class InventoryUpdate {

    // Classes.
    private final static Class<?> CRAFT_PLAYER_CLASS;
    private final static Class<?> PACKET_PLAY_OUT_OPEN_WINDOW_CLASS;
    private final static Class<?> I_CHAT_BASE_COMPONENT_CLASS;
    private final static Class<?> CONTAINERS_CLASS;
    private final static Class<?> ENTITY_PLAYER_CLASS;
    private final static Class<?> CONTAINER_CLASS;
    private final static Class<?> CRAFT_CHAT_MESSAGE_CLASS;

    // Methods.
    private final static MethodHandle getHandle;
    private final static MethodHandle getBukkitView;
    private final static MethodHandle fromJSONOrString;

    // Constructors.
    private static Constructor<?> packetPlayOutOpenWindowConstructor;

    // Fields.
    private static Field activeContainerField;
    private static Field windowIdField;

    static {
        // Initialize classes.
        CRAFT_PLAYER_CLASS = Reflection.getOBC("entity.CraftPlayer");
        CRAFT_CHAT_MESSAGE_CLASS = Reflection.getOBC("util.CraftChatMessage");
        PACKET_PLAY_OUT_OPEN_WINDOW_CLASS = Reflection.getNMS("network.protocol.game", "PacketPlayOutOpenWindow");
        I_CHAT_BASE_COMPONENT_CLASS = Reflection.getNMS("network.chat", "IChatBaseComponent");
        CONTAINERS_CLASS = Reflection.getNMS("world.inventory", "Containers");
        ENTITY_PLAYER_CLASS = Reflection.getNMS("server.level", "EntityPlayer");
        CONTAINER_CLASS = Reflection.getNMS("world.inventory", "Container");

        MethodHandle handle = null, bukkitView = null, jsonOrString = null;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            // Initialize methods.
            handle = lookup.findVirtual(CRAFT_PLAYER_CLASS, "getHandle", MethodType.methodType(ENTITY_PLAYER_CLASS));
            bukkitView = lookup.findVirtual(CONTAINER_CLASS, "getBukkitView", MethodType.methodType(InventoryView.class));
            jsonOrString = lookup.findStatic(CRAFT_CHAT_MESSAGE_CLASS, "fromJSONOrString", MethodType.methodType(I_CHAT_BASE_COMPONENT_CLASS, String.class));

            // Initialize constructors.
            packetPlayOutOpenWindowConstructor = PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, CONTAINERS_CLASS, I_CHAT_BASE_COMPONENT_CLASS);

            // Initialize fields.
            int version = ServerVersion.getVersion().getMinor();
            int patchVersion = ServerVersion.getVersion().getPatch();
            activeContainerField = ENTITY_PLAYER_CLASS.getField(switch (version) {
                case 17 -> "bV";
                case 18 -> switch (patchVersion) {
                    case 0, 1 -> "bW";
                    default -> "bV";
                };
                case 19 -> switch (patchVersion) {
                    case 0, 1, 2, 3 -> "bU";
                    default -> "bP";
                };
                default -> "activeContainer";
            });
            windowIdField = CONTAINER_CLASS.getField("j");
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
        getHandle = handle;
        getBukkitView = bukkitView;
        fromJSONOrString = jsonOrString;
    }

    /**
     * Update the player inventory, so you can change the title.
     *
     * @deprecated Spigot introduced the InventoryView#setTitle(String) method in 1.20 rendering this redundant.
     * @param player   whose inventory will be updated.
     * @param newTitle the new title for the inventory.
     */
    @Deprecated
    public static void updateInventory(Plugin plugin, Player player, String newTitle) {
        Preconditions.checkNotNull(player, "Cannot update inventory to null player.");
        updateInventory(plugin, player, Component.text(newTitle == null ? "" : newTitle));
    }

    /**
     * Update the player inventory, so you can change the title.
     *
     * @deprecated Spigot introduced the InventoryView#setTitle(String) method in 1.20 rendering this redundant.
     * @param plugin   The plugin that caused the update.
     * @param player   whose inventory will be updated.
     * @param newTitle the new title for the inventory.

     */
    @Deprecated
    public static void updateInventory(Plugin plugin, Player player, Component newTitle) {
        Preconditions.checkNotNull(player, "Cannot update inventory to null player.");
        try {
            // Get EntityPlayer from CraftPlayer.
            Object craftPlayer = CRAFT_PLAYER_CLASS.cast(player);
            Object entityPlayer = getHandle.invoke(craftPlayer);

            // Create new title.
            Object title = fromJSONOrString.invoke(BukkitComponentSerializer.gson().serialize(newTitle));
            // Get activeContainer from EntityPlayer.
            Object activeContainer = activeContainerField.get(entityPlayer);
            // Get windowId from activeContainer.
            Integer windowId = (Integer) windowIdField.get(activeContainer);
            // Get InventoryView from activeContainer.
            Object bukkitView = getBukkitView.invoke(activeContainer);
            if (!(bukkitView instanceof InventoryView view)) return;
            InventoryType type = view.getTopInventory().getType();

            // You can't reopen crafting, creative and player inventory.
            if (Arrays.asList("CRAFTING", "CREATIVE", "PLAYER").contains(type.name())) return;

            int size = view.getTopInventory().getSize();

            // Get container, check is not null.
            Containers container = Containers.getType(type, size);
            if (container == null) return;

            // If the container was added in a newer versions than the current, return.
            if (container.getContainerVersion() > ServerVersion.getVersion().getMinor()) {
                Bukkit.getLogger().warning(String.format(
                        "[%s] This container doesn't work on your current version.",
                        plugin.getDescription().getName()));
                return;
            }
            Object object = container.getObject();
            // Create packet.
            Object packet = packetPlayOutOpenWindowConstructor.newInstance(windowId, object, title);

            // Send packet sync.
            Reflection.sendPacket(player, packet);

            // Update inventory.
            player.updateInventory();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * An enum class for the necessaries containers.
     */
    private enum Containers {
        GENERIC_9X1(14, "minecraft:chest", "CHEST"),
        GENERIC_9X2(14, "minecraft:chest", "CHEST"),
        GENERIC_9X3(14, "minecraft:chest", "CHEST", "ENDER_CHEST", "BARREL"),
        GENERIC_9X4(14, "minecraft:chest", "CHEST"),
        GENERIC_9X5(14, "minecraft:chest", "CHEST"),
        GENERIC_9X6(14, "minecraft:chest", "CHEST"),
        GENERIC_3X3(14, null, "DISPENSER", "DROPPER"),
        ANVIL(14, "minecraft:anvil", "ANVIL"),
        BEACON(14, "minecraft:beacon", "BEACON"),
        BREWING_STAND(14, "minecraft:brewing_stand", "BREWING"),
        ENCHANTMENT(14, "minecraft:enchanting_table", "ENCHANTING"),
        FURNACE(14, "minecraft:furnace", "FURNACE"),
        HOPPER(14, "minecraft:hopper", "HOPPER"),
        MERCHANT(14, "minecraft:villager", "MERCHANT"),
        // For an unknown reason, when updating a shulker box, the size of the inventory get a little bigger.
        SHULKER_BOX(14, "minecraft:blue_shulker_box", "SHULKER_BOX"),

        // Added in 1.14, so only works with containers.
        BLAST_FURNACE(14, null, "BLAST_FURNACE"),
        CRAFTING(14, null, "WORKBENCH"),
        GRINDSTONE(14, null, "GRINDSTONE"),
        LECTERN(14, null, "LECTERN"),
        LOOM(14, null, "LOOM"),
        SMOKER(14, null, "SMOKER"),
        // CARTOGRAPHY in 1.14, CARTOGRAPHY_TABLE in 1.15 & 1.16 (container), handle in getObject().
        CARTOGRAPHY_TABLE(14, null, "CARTOGRAPHY"),
        STONECUTTER(14, null, "STONECUTTER"),

        // Added in 1.14, functional since 1.16.
        SMITHING(16, null, "SMITHING");

        private final int containerVersion;
        private final String minecraftName;
        private final String[] inventoryTypesNames;

        private final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        Containers(int containerVersion, String minecraftName, String... inventoryTypesNames) {
            this.containerVersion = containerVersion;
            this.minecraftName = minecraftName;
            this.inventoryTypesNames = inventoryTypesNames;
        }

        /**
         * Get the container based on the current open inventory of the player.
         *
         * @param type type of inventory.
         * @return the container.
         */
        public static Containers getType(InventoryType type, int size) {
            if (type == InventoryType.CHEST) {
                return Containers.valueOf("GENERIC_9X" + size / 9);
            }
            for (Containers container : Containers.values()) {
                for (String bukkitName : container.getInventoryTypesNames()) {
                    if (bukkitName.equalsIgnoreCase(type.toString())) {
                        return container;
                    }
                }
            }
            return null;
        }

        /**
         * Get the object from the container enum.
         *
         * @return a Containers object if 1.14+, otherwise, a String.
         */
        public Object getObject() {
            try {
                int version = ServerVersion.getVersion().getMinor();
                String name = (version == 14 && this == CARTOGRAPHY_TABLE) ? "CARTOGRAPHY" : name();
                // Since 1.17, containers go from "a" to "x".
                if (version > 16) name = String.valueOf(alphabet[ordinal()]);
                Field field = CONTAINERS_CLASS.getField(name);
                return field.get(null);
            } catch (ReflectiveOperationException exception) {
                exception.printStackTrace();
            }
            return null;
        }

        /**
         * Get the version in which the inventory container was added.
         *
         * @return the version.
         */
        public int getContainerVersion() {
            return containerVersion;
        }

        /**
         * Get the name of the inventory from Minecraft for older versions.
         *
         * @return name of the inventory.
         */
        public String getMinecraftName() {
            return minecraftName;
        }

        /**
         * Get inventory types names of the inventory.
         *
         * @return bukkit names.
         */
        public String[] getInventoryTypesNames() {
            return inventoryTypesNames;
        }
    }
}
