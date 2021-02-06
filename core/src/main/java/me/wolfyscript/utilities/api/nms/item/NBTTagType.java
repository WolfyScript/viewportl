package me.wolfyscript.utilities.api.nms.item;

public interface NBTTagType<T extends NBTBase> {

    T of(Object nbtBase);

}
