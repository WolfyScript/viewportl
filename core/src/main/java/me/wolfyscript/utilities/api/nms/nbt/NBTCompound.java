package me.wolfyscript.utilities.api.nms.nbt;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface NBTCompound extends NBTBase {

    Set<String> getKeys();

    int size();

    boolean hasKey(String key);

    boolean hasKeyOfType(String key, int typeId);

    @Nullable
    NBTBase set(String key, NBTBase nbtBase);

    void setByte(String key, byte value);

    void setBoolean(String key, boolean value);

    void setShort(String key, short value);

    void setInt(String key, int value);

    void setLong(String key, long value);

    void setFloat(String key, float value);

    void setDouble(String key, double value);

    void setString(String key, String value);

    void setByteArray(String key, byte[] value);

    void setIntArray(String key, int[] value);

    void setLongArray(String key, long[] value);

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
