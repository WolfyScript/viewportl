package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagList;

public class NBTTagListImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagList> implements NBTTagList {

    public static final NBTTagType<NBTTagList> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagList ? new NBTTagListImpl((net.minecraft.server.v1_16_R3.NBTTagList) nbtBase) : null;

    NBTTagListImpl(net.minecraft.server.v1_16_R3.NBTTagList nbtBase) {
        super(nbtBase);
    }

    public static NBTTagList of() {
        return new NBTTagListImpl(new net.minecraft.server.v1_16_R3.NBTTagList());
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
