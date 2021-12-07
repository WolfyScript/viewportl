package me.wolfyscript.utilities.api.nms.v1_17_R1_P0.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;

public abstract class NBTBaseImpl<NBT extends net.minecraft.nbt.Tag> implements NBTBase {

    protected final NBT nbt;

    NBTBaseImpl(NBT nbtBase) {
        this.nbt = nbtBase;
    }

    @Override
    public byte getTypeId() {
        return nbt.getId();
    }

    @Override
    public String toString() {
        return nbt.toString();
    }

    public NBT getNbt() {
        return nbt;
    }
}
