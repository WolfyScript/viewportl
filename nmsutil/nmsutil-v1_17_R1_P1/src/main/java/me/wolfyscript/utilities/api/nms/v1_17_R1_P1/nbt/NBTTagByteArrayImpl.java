package me.wolfyscript.utilities.api.nms.v1_17_R1_P1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagByteArray;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.ByteArrayTag;

public class NBTTagByteArrayImpl extends NBTBaseImpl<ByteArrayTag> implements NBTTagByteArray {

    public static final NBTTagType<NBTTagByteArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE_ARRAY, nbtBase -> new NBTTagByteArrayImpl((ByteArrayTag) nbtBase));

    NBTTagByteArrayImpl(ByteArrayTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByteArray of(byte[] value) {
        return new NBTTagByteArrayImpl(new ByteArrayTag(value));
    }

    @Override
    public NBTTagType<NBTTagByteArray> getType() {
        return TYPE;
    }
}
