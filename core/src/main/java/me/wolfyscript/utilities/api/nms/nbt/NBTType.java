package me.wolfyscript.utilities.api.nms.nbt;

public enum NBTType {

    TAG_END,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BYTE_ARRAY,
    STRING,
    LIST,
    COMPOUND,
    INT_ARRAY,
    LONG_ARRAY;

    public static NBTType of(int typeId) {
        if (typeId >= 0 && typeId < values().length) {
            return NBTType.values()[typeId];
        }
        throw new IllegalArgumentException("Invalid tag id: " + typeId);
    }

    public boolean is(int typeId) {
        return this.ordinal() == typeId;
    }

    public int getTypeId() {
        return this.ordinal();
    }

}
