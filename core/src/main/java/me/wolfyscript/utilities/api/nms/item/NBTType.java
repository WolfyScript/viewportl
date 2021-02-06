package me.wolfyscript.utilities.api.nms.item;

public enum NBTType {

    TAG_END(0),
    BYTE(1),
    BOOLEAN(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11),
    LONG_ARRAY(12);

    private final int typeId;

    NBTType(int id) {
        this.typeId = id;
    }

    public boolean is(int typeId) {
        return this.typeId == typeId;
    }

    public int getTypeId() {
        return this.typeId;
    }

}
