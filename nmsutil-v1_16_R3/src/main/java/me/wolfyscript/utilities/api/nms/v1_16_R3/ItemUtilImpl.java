package me.wolfyscript.utilities.api.nms.v1_16_R3;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wolfyscript.utilities.api.nms.ItemUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.item.NBTItem;
import me.wolfyscript.utilities.api.nms.v1_16_R3.item.NBTItemImpl;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemUtilImpl extends ItemUtil {


    protected ItemUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    public NBTItem getNBTItem(org.bukkit.inventory.ItemStack bukkitItemStack) {
        return new NBTItemImpl(bukkitItemStack);
    }

    @Override
    public String getItemStackJson(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.save(new NBTTagCompound()).toString();
    }

    @Override
    public org.bukkit.inventory.ItemStack getJsonItemStack(String json) {
        try {
            NBTTagCompound nbtTagCompound = MojangsonParser.parse(json);
            ItemStack itemStack = ItemStack.a(nbtTagCompound);
            //TODO: Automatically convert item to new version using itemStack.convert(int version);
            return CraftItemStack.asBukkitCopy(itemStack);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getItemStackBase64(org.bukkit.inventory.ItemStack itemStack) throws IOException {
        if (itemStack == null) return "null";
        ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NBTCompressedStreamTools.a(nmsItemStack.save(new NBTTagCompound()), outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    @Override
    public org.bukkit.inventory.ItemStack getBase64ItemStack(String data) throws IOException {
        return getBase64ItemStack(Base64.getDecoder().decode(data));
    }

    @Override
    public org.bukkit.inventory.ItemStack getBase64ItemStack(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        NBTTagCompound nbtTagCompound = NBTCompressedStreamTools.a(inputStream);
        ItemStack itemStack = ItemStack.a(nbtTagCompound);
        if (itemStack != null) {
            return CraftItemStack.asBukkitCopy(itemStack);
        }
        return null;
    }
}
