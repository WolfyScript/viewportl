package me.wolfyscript.utilities.api.utils.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.equipment.ArmorType;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {


    private static final Class<?> craftItemStackClazz = Reflection.getOBC("inventory.CraftItemStack");
    private static final Class<?> nmsItemStackClazz = Reflection.getNMS("ItemStack");
    private static final Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");


    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     * Or to save it in the vanilla style Json String.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    public static String convertItemStackToJson(ItemStack itemStack) {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        Method asNMSCopyMethod = Reflection.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");
        Method saveNmsItemStackMethod = Reflection.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.getDeclaredConstructor().newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            WUPlugin.getWolfyUtilities().sendConsoleMessage("failed to serialize itemstack to nms item");
            WUPlugin.getWolfyUtilities().sendConsoleMessage(e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                WUPlugin.getWolfyUtilities().sendConsoleMessage(element.toString());
            }
            return null;
        }
        // Return a string representation of the serialized object
        return itemAsJsonObject.toString();
    }

    /**
     * Converts the NMS Json Sting to an {@link org.bukkit.inventory.ItemStack}.
     *
     * @param json the json to convert
     * @return the ItemStack representation of the Json String
     */
    public static ItemStack convertJsontoItemStack(String json) {
        Class<?> mojangParser = Reflection.getNMS("MojangsonParser");
        Method parseMethod = Reflection.getMethod(mojangParser, "parse", String.class);
        Method aMethod = Reflection.getMethod(nmsItemStackClazz, "a", nbtTagCompoundClazz);
        Method asBukkitCopyMethod = Reflection.getMethod(craftItemStackClazz, "asBukkitCopy", nmsItemStackClazz);

        Object nmsNbtTagCompoundObj;
        Object nmsItemStackObj;
        try {
            nmsNbtTagCompoundObj = parseMethod.invoke(null, json);
            nmsItemStackObj = aMethod.invoke(null, nmsNbtTagCompoundObj);
            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsItemStackObj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is a quick method to serialize ItemStacks into binary encoded in Base64!
     * Downsides of this method:
     * - unreadable
     * - itemstack cannot be edited when this is used inside a config
     * - not very space efficient. Way larger than using the jsonSerializer!
     *
     * @param is ItemStack to serialize
     * @return Base64 String of the ItemStack binary
     */
    @Deprecated
    public static String serializeItemStackBase64(ItemStack is) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
            bukkitOutputStream.writeObject(is);
            bukkitOutputStream.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize ItemStack!", e);
        }
    }

    /**
     * Deserializes the Base64 String to ItemStack.
     *
     * @param data Base64 encoded ItemStack binary
     * @return ItemStack of the Base64 String
     */
    @Deprecated
    public static ItemStack deserializeItemStackBase64(String data) {
        return deserializeItemStackBase64(Base64.getDecoder().decode(data));
    }

    /**
     *
     *
     * @param bytes
     * @return
     */
    @Deprecated
    public static ItemStack deserializeItemStackBase64(byte[] bytes) {
        try {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream);
                Object itemStack = bukkitInputStream.readObject();
                if (itemStack instanceof ItemStack) {
                    return (ItemStack) itemStack;
                }
            } catch (StreamCorruptedException ex) {
                return deserializeNMSItemStack(bytes);
            }
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Combines {@link #convertItemStackToJson(ItemStack)} and {@link #serializeItemStackBase64(ItemStack)} as it
     * converts the ItemStack to an CraftItemStack and then into Base64 encoded binary.
     *
     * @param itemStack
     * @return
     */
    public static String serializeNMSItemStack(ItemStack itemStack) {
        if (itemStack == null) return "null";
        ByteArrayOutputStream outputStream = null;
        try {
            Class<?> nbtTagCompoundClass = Reflection.getNMS("NBTTagCompound");
            Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = Reflection.getOBC("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Reflection.getNMS("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            outputStream = new ByteArrayOutputStream();
            Reflection.getNMS("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, OutputStream.class).invoke(null, nbtTagCompound, outputStream);
        } catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static ItemStack deserializeNMSItemStack(String data) {
        return deserializeNMSItemStack(Base64.getDecoder().decode(data));
    }

    public static ItemStack deserializeNMSItemStack(byte[] bytes) {
        if (bytes == null) return null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Class<?> nbtTagCompoundClass = Reflection.getNMS("NBTTagCompound");
        Class<?> nmsItemStackClass = Reflection.getNMS("ItemStack");
        Object nbtTagCompound;
        ItemStack itemStack = null;
        try {
            nbtTagCompound = Reflection.getNMS("NBTCompressedStreamTools").getMethod("a", InputStream.class).invoke(null, inputStream);
            Object craftItemStack = nmsItemStackClass.getMethod("a", nbtTagCompoundClass).invoke(nmsItemStackClass, nbtTagCompound);
            itemStack = (ItemStack) Reflection.getOBC("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    //SOME ITEMMETA UTILS
    public static boolean isEquipable(Material material){
        if (material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") || material.name().endsWith("_HELMET") || material.name().endsWith("_BOOTS") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL")) {
            return true;
        }
        switch (material) {
            case ELYTRA:
            case CARVED_PUMPKIN:
                return true;
        }
        return false;
    }

    public static boolean isEquipable(Material material, ArmorType type) {
        switch (type) {
            case HELMET:
                return material.name().endsWith("_HELMET") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL") || material.equals(Material.CARVED_PUMPKIN);
            case CHESTPLATE:
                return material.equals(Material.ELYTRA) || material.name().endsWith("_CHESTPLATE");
            case LEGGINGS:
                return material.name().endsWith("_LEGGINGS");
            case BOOTS:
                return material.name().endsWith("_BOOTS");
        }
        return false;
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    public static boolean isAirOrNull(CustomItem item) {
        return item == null || item.getApiReference() == null || isAirOrNull(item.getItemStack());
    }

    public static boolean isTool(Material material) {
        return material.name().endsWith("AXE") || material.name().endsWith("HOE") || material.name().endsWith("SWORD") || material.name().endsWith("SHOVEL") || material.name().endsWith("PICKAXE");
    }

    /*
    Prepare and configure the ItemStack for the GUI!
     */
    public static ItemStack[] createItem(ItemStack itemStack, String displayName, String[] helpLore, String... normalLore) {
        ItemStack[] itemStacks = new ItemStack[2];
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(itemMeta != null){
            if(displayName != null && !displayName.isEmpty()){
                itemMeta.setDisplayName(WolfyUtilities.translateColorCodes(displayName));
            }
            if (normalLore != null && normalLore.length > 0) {
                lore = Arrays.stream(normalLore).map(row -> row.equalsIgnoreCase("<empty>") ? "" : WolfyUtilities.translateColorCodes(row)).collect(Collectors.toList());
                itemMeta.setLore(lore);
            }
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemStack.setItemMeta(itemMeta);
        }
        itemStacks[0] = itemStack;
        ItemStack helpItem = new ItemStack(itemStack);
        ItemMeta helpMeta = helpItem.getItemMeta();
        if(helpMeta != null){
            if (helpLore != null && helpLore.length > 0) {
                lore.addAll(Arrays.stream(helpLore).map(row -> row.equalsIgnoreCase("<empty>") ? "" : WolfyUtilities.translateColorCodes(row)).collect(Collectors.toList()));
            }
            helpMeta.setLore(lore);
            helpItem.setItemMeta(helpMeta);
        }
        itemStacks[1] = helpItem;
        return itemStacks;
    }
}
