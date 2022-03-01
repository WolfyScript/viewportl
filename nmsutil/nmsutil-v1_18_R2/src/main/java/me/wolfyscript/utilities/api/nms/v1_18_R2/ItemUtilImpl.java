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

package me.wolfyscript.utilities.api.nms.v1_18_R2;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wolfyscript.utilities.api.nms.ItemUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemUtilImpl extends ItemUtil {


    protected ItemUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public String getItemStackJson(org.bukkit.inventory.ItemStack itemStack) {
        var nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.save(new CompoundTag()).toString();
    }

    @Override
    public org.bukkit.inventory.ItemStack getJsonItemStack(String json) {
        try {
            var nbtTagCompound = TagParser.parseTag(json);
            var itemStack = ItemStack.of(nbtTagCompound);
            return CraftItemStack.asBukkitCopy(itemStack);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getItemStackBase64(org.bukkit.inventory.ItemStack itemStack) throws IOException {
        if (itemStack == null) return "null";
        var nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        var outputStream = new ByteArrayOutputStream();
        NbtIo.writeCompressed(nmsItemStack.save(new CompoundTag()), outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    @Override
    public org.bukkit.inventory.ItemStack getBase64ItemStack(String data) throws IOException {
        return getBase64ItemStack(Base64.getDecoder().decode(data));
    }

    @Override
    public org.bukkit.inventory.ItemStack getBase64ItemStack(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        var inputStream = new ByteArrayInputStream(bytes);
        var nbtTagCompound = NbtIo.readCompressed(inputStream);
        var itemStack = ItemStack.of(nbtTagCompound);
        if (itemStack != null) {
            return CraftItemStack.asBukkitCopy(itemStack);
        }
        return null;
    }
}
