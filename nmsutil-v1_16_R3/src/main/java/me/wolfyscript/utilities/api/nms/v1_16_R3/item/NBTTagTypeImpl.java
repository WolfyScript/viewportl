package me.wolfyscript.utilities.api.nms.v1_16_R3.item;

import me.wolfyscript.utilities.api.nms.item.NBTBase;
import me.wolfyscript.utilities.api.nms.item.NBTTagEnd;
import me.wolfyscript.utilities.api.nms.item.NBTTagType;

public abstract class NBTTagTypeImpl<T extends NBTBase> implements NBTTagType<T> {

    public static NBTTagType<NBTTagEnd> invalidType(int typeId) {
        return new NBTTagType<NBTTagEnd>() {

            @Override
            public NBTTagEnd of(Object nbtBase) {
                throw new IllegalArgumentException("Invalid tag id: " + typeId);
            }

        };
    }

}
