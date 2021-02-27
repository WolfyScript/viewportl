package me.wolfyscript.utilities.api.nms.nbt;

public interface NBTBase {

    byte getTypeId();

    NBTTagType<?> getType();
}
