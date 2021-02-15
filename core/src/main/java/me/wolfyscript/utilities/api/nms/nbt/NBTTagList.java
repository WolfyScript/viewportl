package me.wolfyscript.utilities.api.nms.nbt;

public interface NBTTagList extends NBTBase {

    NBTCompound getCompound(int index);

    NBTTagList getTagList(int index);

    NBTBase getTag(int index);

    NBTBase remove(int index);

    short getShort(int index);

    int getInt(int index);

    int[] getIntArray(int index);

    double getDouble(int index);

    float getFloat(int index);

    String getString(int index);

    int size();

    NBTBase set(int index, NBTBase value);

    void add(int index, NBTBase value);

    void clear();

}
