package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagString;

public class NBTTagStringImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagString> implements NBTTagString {

    public static final NBTTagType<NBTTagString> TYPE = nbtBase -> nbtBase instanceof net.minecraft.server.v1_16_R3.NBTTagString ? new NBTTagStringImpl((net.minecraft.server.v1_16_R3.NBTTagString) nbtBase) : null;

    NBTTagStringImpl(net.minecraft.server.v1_16_R3.NBTTagString nbtBase) {
        super(nbtBase);
    }

    public static NBTTagString of(String value) {
        return new NBTTagStringImpl(net.minecraft.server.v1_16_R3.NBTTagString.a(value));
    }

    @Override
    public NBTTagType<?> getTagType() {
        return TYPE;
    }

    @Override
    public String asString() {
        return nbt.asString();
    }

    @Override
    public String toString() {
        return nbt.toString();
    }
}
