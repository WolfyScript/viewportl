package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.nbt.NBTItem;

public abstract class NBTUtil extends UtilComponent {

    protected NBTTag nbtTag;

    protected NBTUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * The NBTTag has many methods to create specific Tags.
     *
     * @return The instance of the NBTTag builder.
     */
    public NBTTag getTag() {
        return nbtTag;
    }

    /**
     * Get an instance of the NBTItem interface, which allows you to read and edit the internal NBT Tags of the item.
     * It provides a fully featured NBT API so no need for NMS or Reflection of any kind.
     *
     * @param bukkitItemStack The bukkit ItemStack
     * @return The instance of the NBTItem interface containing an API for NBT Tags
     */
    public abstract NBTItem getItem(org.bukkit.inventory.ItemStack bukkitItemStack);

}
