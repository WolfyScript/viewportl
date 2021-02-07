package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagEnd;

public interface NBTTagType<T extends NBTBase> {

    static NBTTagType<NBTTagEnd> invalidType(int typeId) {
        return nbtBase -> {
            throw new IllegalArgumentException("Invalid tag id: " + typeId);
        };
    }

    T of(net.minecraft.server.v1_16_R3.NBTBase nbtBase);

}
