package me.wolfyscript.utilities.api.utils;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.equipment.ArmorType;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemUtils {

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    public static String convertItemStackToJson(ItemStack itemStack) {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        Class<?> craftItemStackClazz = Reflection.getOBC("inventory.CraftItemStack");
        Method asNMSCopyMethod = Reflection.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        Class<?> nmsItemStackClazz = Reflection.getNMS("ItemStack");
        Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");
        Method saveNmsItemStackMethod = Reflection.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Main.getMainUtil().sendConsoleMessage("failed to serialize itemstack to nms item");
            Main.getMainUtil().sendConsoleMessage(t.toString());
            for (StackTraceElement element : t.getStackTrace()) {
                Main.getMainUtil().sendConsoleMessage(element.toString());
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
        Class<?> craftItemStackClazz = Reflection.getOBC("inventory.CraftItemStack");
        Class<?> nmsItemStackClazz = Reflection.getNMS("ItemStack");
        Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");
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
                for (String row : normalLore) {
                    if (!row.isEmpty()) {
                        lore.add(row.equalsIgnoreCase("<empty>") ? "" : org.bukkit.ChatColor.translateAlternateColorCodes('&', row));
                    }
                }
            }
            if (lore.size() > 0) {
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
                for (String row : helpLore) {
                    if (!row.isEmpty()) {
                        lore.add(row.equalsIgnoreCase("<empty>") ? "" : ChatColor.translateAlternateColorCodes('&', row));
                    }
                }
            }
            helpMeta.setLore(lore);
            helpItem.setItemMeta(helpMeta);
        }
        itemStacks[1] = helpItem;
        return itemStacks;
    }

    /*
    This method may be problematic if using NBT data.
    The data maybe can't be saved and loaded correctly!
     */
    @Deprecated
    public static String serializeItemStack(ItemStack is) {
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

    public static ItemStack deserializeItemStack(String data) {
        return deserializeItemStack(Base64.getDecoder().decode(data));
    }

    public static ItemStack deserializeItemStack(byte[] bytes) {
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
    public static ItemMeta toggleItemFlag(ItemMeta itemMeta, ItemFlag itemFlag){
        if (!itemMeta.hasItemFlag(itemFlag)) {
            itemMeta.addItemFlags(itemFlag);
        } else {
            itemMeta.removeItemFlags(itemFlag);
        }
        return itemMeta;
    }

    public static ItemMeta setEnchantEffect(ItemMeta itemMeta, boolean hide){
        if(!itemMeta.hasEnchants()){
            itemMeta.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 0, true);
            if(hide){
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        return itemMeta;
    }

    /*
    Sets value to the lore. It will be hidden.
    Deprecated, because 1.14 has an better alternative! It can be accessed via ItemMeta.getPersistentDataContainer()!
    Alternative can be found in the CustomItem class!
     */
    @Deprecated
    public static ItemMeta setToItemSettings(ItemMeta itemMeta, String key, Object value) {
        JSONObject obj = getItemSettings(itemMeta);
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        if (obj == null) {
            obj = new JSONObject(new HashMap<String, Object>());
            obj.put(key, value);
            lore.add(WolfyUtilities.hideString("itemSettings" + obj.toString()));
        } else {
            obj.put(key, value);
            for (int i = 0; i < lore.size(); i++) {
                String line = WolfyUtilities.unhideString(lore.get(i));
                if (line.startsWith("itemSettings")) {
                    lore.set(i, WolfyUtilities.hideString("itemSettings" + obj.toString()));
                }
            }
        }
        itemMeta.setLore(lore);
        return itemMeta;
    }

    @Deprecated
    public static ItemStack setToItemSettings(ItemStack itemStack, String key, Object value) {
        itemStack.setItemMeta(setToItemSettings(itemStack.getItemMeta(), key, value));
        return itemStack;
    }

    public static void removeItemSettings(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        removeItemSettings(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static void removeItemSettings(ItemMeta itemMeta){
        if (itemMeta != null && itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            Iterator<String> iterator = lore.iterator();
            while (iterator.hasNext()){
                String cleared = WolfyUtilities.unhideString(iterator.next());
                if (cleared.startsWith("itemSettings")) {
                    iterator.remove();
                }
            }
            itemMeta.setLore(lore);
        }
    }

    @Deprecated
    @Nullable
    public static Object getFromItemSettings(ItemMeta itemMeta, String key) {
        if (hasItemSettings(itemMeta)) {
            return getItemSettings(itemMeta).get(key);
        }
        return null;
    }

    @Deprecated
    public static Object getFromItemSettings(ItemStack itemStack, String key) {
        return getFromItemSettings(itemStack.getItemMeta(), key);
    }

    @Deprecated
    public static boolean isInItemSettings(ItemStack itemStack, String key) {
        return getFromItemSettings(itemStack, key) != null;
    }

    @Deprecated
    public static boolean isInItemSettings(ItemMeta itemMeta, String key) {
        return getFromItemSettings(itemMeta, key) != null;
    }

    @Deprecated
    public static boolean hasItemSettings(@Nonnull ItemStack itemStack) {
        return getItemSettings(itemStack.getItemMeta()) != null;
    }

    @Deprecated
    public static boolean hasItemSettings(@Nullable ItemMeta itemMeta) {
        return getItemSettings(itemMeta) != null;
    }

    @Deprecated
    public static JSONObject getItemSettings(@Nonnull ItemStack itemStack) {
        return getItemSettings(itemStack.getItemMeta());
    }

    @Deprecated
    @Nullable
    public static JSONObject getItemSettings(@Nullable ItemMeta itemMeta) {
        if (itemMeta != null && itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            for (String line : lore) {
                String cleared = WolfyUtilities.unhideString(line);
                if (cleared.startsWith("itemSettings")) {
                    try {
                        JSONObject obj = (JSONObject) new JSONParser().parse(cleared.replace("itemSettings", ""));
                        return obj;
                    } catch (ParseException e) {
                        Main.getMainUtil().sendConsoleWarning("Error getting JSONObject from String:");
                        Main.getMainUtil().sendConsoleWarning("" + cleared);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /*
        Custom Item Damage!
     */

    //itemSettings{"damage":<damage>,"durability":<total_durability>,"durability_tag":""}

    @Deprecated
    public static boolean hasCustomDurability(@Nonnull ItemStack itemStack) {
        return hasCustomDurability(itemStack.getItemMeta());
    }

    @Deprecated
    public static boolean hasCustomDurability(@Nullable ItemMeta itemMeta) {
        JSONObject obj = getItemSettings(itemMeta);
        if (obj != null) {
            return ((Set<String>) obj.keySet()).contains("durability");
        }
        return false;
    }

    /*
    Sets the custom durability to the ItemStack and adds damage of 0 if it not exists.
    Returns the ItemStack with the new lore.
     */
    @Deprecated
    public static void setCustomDurability(ItemStack itemStack, int durability) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setCustomDurability(itemMeta, durability);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static void setCustomDurability(ItemMeta itemMeta, int durability) {
        setToItemSettings(itemMeta, "durability", durability);
        setDurabilityTag(itemMeta);
    }

    @Deprecated
    public static int getCustomDurability(ItemStack itemStack) {
        return getCustomDurability(itemStack.getItemMeta());
    }

    @Deprecated
    public static int getCustomDurability(ItemMeta itemMeta) {
        if (getFromItemSettings(itemMeta, "durability") != null) {
            return NumberConversions.toInt(getFromItemSettings(itemMeta, "durability"));
        }
        return 0;
    }

    @Deprecated
    public static void setDamage(ItemStack itemStack, int damage) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage((int) (itemStack.getType().getMaxDurability() * ((double) damage / (double) getCustomDurability(itemStack))));
        }
        setDamage(itemMeta, damage);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static void setDamage(ItemMeta itemMeta, int damage) {
        setToItemSettings(itemMeta, "damage", damage);
        setDurabilityTag(itemMeta);
    }

    @Deprecated
    public static int getDamage(ItemStack itemStack) {
        return getDamage(itemStack.getItemMeta());
    }

    @Deprecated
    public static int getDamage(ItemMeta itemMeta) {
        if (getFromItemSettings(itemMeta, "damage") != null) {
            int damage = NumberConversions.toInt(getFromItemSettings(itemMeta, "damage"));
            return damage;
        }
        return 0;
    }

    @Deprecated
    public static void setDurabilityTag(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setDurabilityTag(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static void setDurabilityTag(ItemMeta itemMeta) {
        if (!getDurabilityTag(itemMeta).isEmpty() && !getDurabilityTag(itemMeta).equals("")) {
            List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = WolfyUtilities.unhideString(lore.get(i));
                if (line.startsWith("durability_tag")) {
                    lore.remove(i);
                }
            }
            lore.add(lore.size() > 0 ? lore.size() - 1 : 0, WolfyUtilities.hideString("durability_tag") + WolfyUtilities.translateColorCodes(getDurabilityTag(itemMeta).replace("%DUR%", String.valueOf(getCustomDurability(itemMeta) - getDamage(itemMeta))).replace("%MAX_DUR%", String.valueOf(getCustomDurability(itemMeta)))));
            itemMeta.setLore(lore);
        }
    }

    public static void removeDurabilityTag(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        removeDurabilityTag(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static void removeDurabilityTag(ItemMeta itemMeta){
        List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            String line = WolfyUtilities.unhideString(lore.get(i));
            if (line.startsWith("durability_tag")) {
                lore.remove(i);
            }
        }
        itemMeta.setLore(lore);
    }

    @Deprecated
    public static void setDurabilityTag(ItemMeta itemMeta, String value) {
        setToItemSettings(itemMeta, "durability_tag", value);
        setDurabilityTag(itemMeta);
    }

    @Deprecated
    public static void setDurabilityTag(ItemStack itemStack, String value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setDurabilityTag(itemMeta, value);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static String getDurabilityTag(ItemStack itemStack) {
        return getDurabilityTag(itemStack.getItemMeta());
    }

    @Deprecated
    public static String getDurabilityTag(ItemMeta itemMeta) {
        if (getFromItemSettings(itemMeta, "durability_tag") != null) {
            return (String) getFromItemSettings(itemMeta, "durability_tag");
        }
        return "";
    }

    public static boolean isEquipable(Material material){
        if (material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") || material.name().endsWith("_HELMET") || material.name().endsWith("_BOOTS") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL")) {
            return true;
        }
        switch (material) {
            case LEATHER_BOOTS:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case IRON_CHESTPLATE:
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case DIAMOND_CHESTPLATE:
            case ELYTRA:
            case CARVED_PUMPKIN:
            case PLAYER_HEAD:
                return true;
        }
        return false;
    }

    public static boolean isEquipable(Material material, ArmorType type) {
        switch (type) {
            case HELMET:
                return material.name().endsWith("_HELMET") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL");
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
}
