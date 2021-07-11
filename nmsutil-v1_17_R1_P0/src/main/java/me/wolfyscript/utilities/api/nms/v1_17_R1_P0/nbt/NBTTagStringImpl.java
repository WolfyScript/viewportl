package me.wolfyscript.utilities.api.nms.v1_17_R1_P0.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagString;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.StringTag;

public class NBTTagStringImpl extends NBTBaseImpl<StringTag> implements NBTTagString {

    public static final NBTTagType<NBTTagString> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.STRING, nbtBase -> new NBTTagStringImpl((StringTag) nbtBase));

    NBTTagStringImpl(StringTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagString of(String value) {
        return new NBTTagStringImpl(StringTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagString> getType() {
        return TYPE;
    }

    @Override
    public String asString() {
        return nbt.getAsString();
    }

    @Override
    public String toString() {
        return nbt.toString();
    }
}
