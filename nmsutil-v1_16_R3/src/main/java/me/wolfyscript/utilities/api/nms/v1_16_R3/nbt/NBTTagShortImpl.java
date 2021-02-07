package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagShort;

public class NBTTagShortImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagShort> implements NBTTagShort {

    public static final NBTTagType<NBTTagShort> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagShort ? new NBTTagShortImpl((net.minecraft.server.v1_16_R3.NBTTagShort) nbtBase) : null;

    private NBTTagShortImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagShort.a((short) 0));
    }

    NBTTagShortImpl(net.minecraft.server.v1_16_R3.NBTTagShort nbtBase) {
        super(nbtBase);
    }

    public static NBTTagShort of(short value) {
        return new NBTTagShortImpl(net.minecraft.server.v1_16_R3.NBTTagShort.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }
}
