package me.wolfyscript.utilities.api.nms.nbt;

public interface NBTNumber extends NBTBase {

    long asLong();

    int asInt();

    short asShort();

    byte asByte();

    double asDouble();

    float asFloat();
}
