package me.wolfyscript.utilities.api.nms.v1_16_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagString;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;

public class NBTTagStringImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R1.NBTTagString> implements NBTTagString {

    public static final NBTTagType<NBTTagString> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.STRING, nbtBase -> new NBTTagStringImpl((net.minecraft.server.v1_16_R1.NBTTagString) nbtBase));

    NBTTagStringImpl(net.minecraft.server.v1_16_R1.NBTTagString nbtBase) {
        super(nbtBase);
    }

    public static NBTTagString of(String value) {
        return new NBTTagStringImpl(net.minecraft.server.v1_16_R1.NBTTagString.a(value));
    }

    @Override
    public NBTTagType<NBTTagString> getType() {
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
