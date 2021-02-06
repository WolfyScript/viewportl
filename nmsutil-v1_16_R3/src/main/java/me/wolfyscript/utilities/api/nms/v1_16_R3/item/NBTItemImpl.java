package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTCompound;
import me.wolfyscript.utilities.api.nms.item.NBTItem;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTItemImpl extends NBTItem {

    final NBTTagCompound compound;
    private final net.minecraft.server.v1_16_R3.ItemStack nms;

    public NBTItemImpl(ItemStack bukkitItemStack) {
        super(bukkitItemStack);
        this.nms = CraftItemStack.asNMSCopy(bukkitItemStack);
        this.compound = this.nms.hasTag() ? this.nms.getTag() : new NBTTagCompound();
    }

    @Override
    public ItemStack create() {
        nms.setTag(compound);
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public NBTCompound getCompound(String key) {
        return new NBTTagCompoundImpl(this, key);
    }

    public void setCompound(String key, NBTCompound nbtCompound) {


    }

    @Override
    public boolean hasKey(String key) {
        return compound.hasKey(key);
    }

    @Override
    public boolean hasKeyOfType(String key, int typeId) {
        return compound.hasKeyOfType(key, typeId);
    }


}
