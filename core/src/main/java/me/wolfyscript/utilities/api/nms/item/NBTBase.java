package me.wolfyscript.utilities.api.nms.item;

public interface NBTBase {

    void applyToCompound(NBTCompound compound, String key);

    byte getTypeId();

    NBTTagType<?> getType();
}
