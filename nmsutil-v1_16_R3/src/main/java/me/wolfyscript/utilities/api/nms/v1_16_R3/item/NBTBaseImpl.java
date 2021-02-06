package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTBase;
import me.wolfyscript.utilities.api.nms.item.NBTCompound;

public abstract class NBTBaseImpl<NBT extends net.minecraft.server.v1_16_R3.NBTBase> implements NBTBase {

    protected final NBT nbt;

    NBTBaseImpl(NBT nbtBase) {
        this.nbt = nbtBase;
    }

    @Override
    public void applyToCompound(NBTCompound compound, String key) {
        compound.set(key, (NBTBase) nbt);
    }

    @Override
    public byte getTypeId() {
        return nbt.getTypeId();
    }

}
