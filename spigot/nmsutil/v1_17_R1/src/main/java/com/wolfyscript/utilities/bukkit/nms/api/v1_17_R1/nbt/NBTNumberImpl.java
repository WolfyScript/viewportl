package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1.nbt;

public abstract class NBTNumberImpl<NBT extends net.minecraft.nbt.NumericTag> extends NBTBaseImpl<NBT> implements com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTNumber {

    NBTNumberImpl(NBT nbtBase) {
        super(nbtBase);
    }

    @Override
    public long asLong() {
        return nbt.getAsLong();
    }

    @Override
    public int asInt() {
        return nbt.getAsInt();
    }

    @Override
    public short asShort() {
        return nbt.getAsShort();
    }

    @Override
    public byte asByte() {
        return nbt.getAsByte();
    }

    @Override
    public double asDouble() {
        return nbt.getAsDouble();
    }

    @Override
    public float asFloat() {
        return nbt.getAsFloat();
    }
}
