package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTBase;
import me.wolfyscript.utilities.api.nms.item.NBTCompound;
import me.wolfyscript.utilities.api.nms.item.NBTTagType;
import net.minecraft.server.v1_16_R3.NBTTagCompound;

import java.util.Set;

public class NBTTagCompoundImpl extends NBTBaseImpl<NBTTagCompound> implements NBTCompound {

    public static final NBTTagType<NBTCompound> TYPE = new NBTTagTypeImpl<NBTCompound>() {

        @Override
        public NBTCompound of(Object nbtBase) {
            return nbtBase instanceof NBTTagCompound ? new NBTTagCompoundImpl((NBTTagCompound) nbtBase) : null;
        }

    };

    private NBTTagCompoundImpl() {
        super(new NBTTagCompound());
    }

    private NBTTagCompoundImpl(NBTTagCompound compound) {
        super(compound);
    }

    NBTTagCompoundImpl(NBTTagCompoundImpl parent, String key) {
        super(parent.hasKeyOfType(key, 10) ? parent.nbt.getCompound(key) : new NBTTagCompound());
    }

    NBTTagCompoundImpl(NBTItemImpl item, String key) {
        super(item.hasKeyOfType(key, 10) ? item.compound.getCompound(key) : new NBTTagCompound());
    }

    @Override
    public Set<String> getKeys() {
        return nbt.getKeys();
    }

    @Override
    public int size() {
        return nbt.e();
    }

    @Override
    public boolean hasKey(String key) {
        return nbt.hasKey(key);
    }

    @Override
    public boolean hasKeyOfType(String key, int typeId) {
        return nbt.hasKeyOfType(key, typeId);
    }

    @Override
    public void set(String key, me.wolfyscript.utilities.api.nms.item.NBTBase nbtBase) {
        nbtBase.applyToCompound(this, key);
    }

    @Override
    public NBTBase get(String key) {
        net.minecraft.server.v1_16_R3.NBTBase base = nbt.get(key);
        return base != null ? NBTTagTypes.of(base.getTypeId()).of(base) : null;
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
    public NBTTagType<?> getType() {
        return TYPE;
    }
}
