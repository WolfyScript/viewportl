package me.wolfyscript.utilities.api.utils;

import com.google.common.io.BaseEncoding;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.main.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public static String serializeItemStack(ItemStack is) {
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
            bukkitOutputStream.writeObject(is);
            bukkitOutputStream.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize ItemStack!", e);
        }
    }

    public static ItemStack deserializeItemStack(String data){
        return deserializeItemStack(Base64.getDecoder().decode(data));
    }

    public static ItemStack deserializeItemStack(byte[] bytes) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream);
            ItemStack decoded = (ItemStack) bukkitInputStream.readObject();
            return decoded;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static ItemStack setToCCSettings(ItemStack itemStack, String key, Object value){
        JSONObject obj = getCCSettings(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        if(obj == null){
            obj = new JSONObject(new HashMap<String, Object>());
            obj.put(key, value);
            lore.add(WolfyUtilities.hideString("itemSettings"+obj.toString()));
        }else{
            System.out.println("Set: "+key+" -> "+value);
            obj.put(key, value);
            for(int i = 0; i < lore.size(); i++){
                String line = WolfyUtilities.unhideString(lore.get(i));
                if(line.startsWith("itemSettings")){
                    lore.remove(i);
                }
            }
            lore.add(WolfyUtilities.hideString("itemSettings"+obj.toString()));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Nullable
    public static Object getFromCCSettings(ItemStack itemStack, String key){
        if(hasCCSettings(itemStack)){
            return getCCSettings(itemStack).get(key);
        }
        return null;
    }

    public static boolean isInCCSettings(ItemStack itemStack, String key){
        return getFromCCSettings(itemStack, key) != null;
    }

    public static boolean hasCCSettings(ItemStack itemStack){
        return getCCSettings(itemStack) != null;
    }

    public static JSONObject getCCSettings(ItemStack itemStack){
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
            List<String> lore = itemStack.getItemMeta().getLore();
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

    //cc_settings{"damage":<damage>,"durability":<total_durability>}

    public static boolean hasCustomDurability(ItemStack itemStack) {
        JSONObject obj = getCCSettings(itemStack);
        if (obj != null) {
            return ((Set<String>) obj.keySet()).contains("durability");
        }
        return false;
    }

    public static ItemStack setCustomDurability(ItemStack itemStack, long durability) {
        if(isInCCSettings(itemStack, "damage")){
            setToCCSettings(itemStack, "damage", 0);
        }
        return setToCCSettings(itemStack, "durability", durability);
    }

    public static long getCustomDurability(ItemStack itemStack){
        if(getFromCCSettings(itemStack, "durability") != null){
            return (long) getFromCCSettings(itemStack, "durability");
        }
        return 0;
    }

    public static ItemStack setDamage(ItemStack itemStack, long damage){
        return setToCCSettings(itemStack, "damage", damage);
    }

    public static long getDamage(ItemStack itemStack){
        if(getFromCCSettings(itemStack, "damage") != null){
            return (long) getFromCCSettings(itemStack, "damage");
        }
        return 0;
    }
}
