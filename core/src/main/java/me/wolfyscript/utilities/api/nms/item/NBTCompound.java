package me.wolfyscript.utilities.api.nms.item;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface NBTCompound extends NBTBase {

    Set<String> getKeys();

    int size();

    boolean hasKey(String key);

    boolean hasKeyOfType(String key, int typeId);

    void set(String key, NBTBase nbtBase);

    @Nullable
    NBTBase get(String key);

    byte getByte(String key);

    boolean getBoolean(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getString(String key);

    byte[] getByteArray(String key);

    int[] getIntArray(String key);

    long[] getLongArray(String key);

    NBTCompound getCompound(String key);

}
