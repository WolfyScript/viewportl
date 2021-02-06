package me.wolfyscript.utilities.api.nms.item;

public interface NBTNumber extends NBTBase {

    long asLong();

    int asInt();

    short asShort();

    byte asByte();

    double asDouble();

    float asFloat();
}
