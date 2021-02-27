package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.nbt.*;

public abstract class NBTTag {

    public abstract NBTTagEnd end();

    public abstract NBTCompound compound();

    public abstract NBTTagList list();

    public abstract NBTTagByte ofByte(byte value);

    public NBTTagByte ofByte(boolean value) {
        return value ? ofByte((byte) 1) : ofByte((byte) 0);
    }

    public abstract NBTTagByteArray ofByteArray(byte[] value);

    public abstract NBTTagDouble ofDouble(double value);

    public abstract NBTTagFloat ofFloat(float value);

    public abstract NBTTagInt ofInt(int value);

    public abstract NBTTagIntArray ofIntArray(int[] array);

    public abstract NBTTagLong ofLong(long value);

    public abstract NBTTagLongArray ofLongArray(long[] array);

    public abstract NBTTagShort ofShort(short value);

    public abstract NBTTagString ofString(String value);

}
