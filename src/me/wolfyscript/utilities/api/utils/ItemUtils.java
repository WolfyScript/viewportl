package me.wolfyscript.utilities.api.utils;

import com.google.common.io.BaseEncoding;
import me.wolfyscript.utilities.main.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

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
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream)) {
            bukkitOutputStream.writeObject(is);
            return BaseEncoding.base64().encode(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize ItemStack!", e);
        }
    }

    public static ItemStack deserializeItemStack(String data){
        return deserializeItemStack(BaseEncoding.base64().decode(data));
    }

    public static ItemStack deserializeItemStack(byte[] bytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream)) {
            ItemStack decoded = (ItemStack) bukkitInputStream.readObject();
            return decoded;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Unable to deserialize ItemStack!", e);
        }
    }
}
