package me.wolfyscript.utilities.api.nms.v1_17_R1_P1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public class NBTTagCompoundImpl extends NBTBaseImpl<CompoundTag> implements NBTCompound {

    public static final NBTTagType<NBTCompound> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.COMPOUND, nbtBase -> new NBTTagCompoundImpl((CompoundTag) nbtBase));

    private NBTTagCompoundImpl() {
        super(new CompoundTag());
    }

    NBTTagCompoundImpl(CompoundTag compound) {
        super(compound);
    }

    NBTTagCompoundImpl(NBTTagCompoundImpl parent, String key) {
        super(parent.hasKeyOfType(key, 10) ? parent.nbt.getCompound(key) : new CompoundTag());
    }

    public static NBTCompound of() {
        return new NBTTagCompoundImpl();
    }

    @Override
    public Set<String> getKeys() {
        return nbt.getAllKeys();
    }

    @Override
    public int size() {
        return nbt.size();
    }

    @Override
    public boolean hasKey(String key) {
        return nbt.contains(key);
    }

    @Override
    public boolean hasKeyOfType(String key, int typeId) {
        return nbt.contains(key, typeId);
    }

    @Override
    public NBTBase set(String key, NBTBase nbtBase) {
        return NBTTagTypes.convert(nbt.put(key, ((NBTBaseImpl<?>) nbtBase).nbt));
    }

    @Override
    public void setByte(String key, byte value) {
        nbt.putByte(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        nbt.putBoolean(key, value);
    }

    @Override
    public void setShort(String key, short value) {
        nbt.putShort(key, value);
    }

    @Override
    public void setInt(String key, int value) {
        nbt.putInt(key, value);
    }

    @Override
    public void setLong(String key, long value) {
        nbt.putLong(key, value);
    }

    @Override
    public void setFloat(String key, float value) {
        nbt.putFloat(key, value);
    }

    @Override
    public void setDouble(String key, double value) {
        nbt.putDouble(key, value);
    }

    @Override
    public void setString(String key, String value) {
        nbt.putString(key, value);
    }

    @Override
    public void setByteArray(String key, byte[] value) {
        nbt.putByteArray(key, value);
    }

    @Override
    public void setIntArray(String key, int[] value) {
        nbt.putIntArray(key, value);
    }

    @Override
    public void setLongArray(String key, long[] value) {
        nbt.putLongArray(key, value);
    }

    @Override
    public NBTBase get(String key) {
        return NBTTagTypes.convert(nbt.get(key));
    }

    @Override
    public byte getByte(String key) {
        return nbt.getByte(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return nbt.getBoolean(key);
    }

    @Override
    public short getShort(String key) {
        return nbt.getShort(key);
    }

    @Override
    public int getInt(String key) {
        return nbt.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return nbt.getLong(key);
    }

    @Override
    public float getFloat(String key) {
        return nbt.getFloat(key);
    }

    @Override
    public double getDouble(String key) {
        return nbt.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return nbt.getString(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return nbt.getByteArray(key);
    }

    @Override
    public int[] getIntArray(String key) {
        return nbt.getIntArray(key);
    }

    @Override
    public long[] getLongArray(String key) {
        return nbt.getLongArray(key);
    }

    @Override
    public NBTCompound getCompound(String key) {
        return new NBTTagCompoundImpl(this, key);
    }

    @Override
    public NBTTagType<NBTCompound> getType() {
        return TYPE;
    }
}
