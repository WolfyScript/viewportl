package me.wolfyscript.utilities.api.nms.v1_16_R2.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagList;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagListImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R2.NBTTagList> implements NBTTagList {

    public static final NBTTagType<NBTTagList> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LIST, nbtBase -> new NBTTagListImpl((net.minecraft.server.v1_16_R2.NBTTagList) nbtBase));

    NBTTagListImpl(net.minecraft.server.v1_16_R2.NBTTagList nbtBase) {
        super(nbtBase);
    }

    public static NBTTagList of() {
        return new NBTTagListImpl(new net.minecraft.server.v1_16_R2.NBTTagList());
    }

    @Override
    public NBTTagType<NBTTagList> getType() {
        return TYPE;
    }
}
