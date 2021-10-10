package me.wolfyscript.utilities.api.nms.nbt;

import org.jetbrains.annotations.Nullable;

public interface NBTTagType<T extends NBTBase> {

    static NBTTagType<NBTTagEnd> invalidType(int typeId) {
        return new NBTTagType<>() {
            @Override
            public NBTTagEnd get(Object nbtBase) {
                throw new IllegalArgumentException("Invalid tag id: " + typeId);
            }

            @Override
            public Type getType() {
                return Type.TAG_END;
            }
        };
    }

    T get(@Nullable Object nbtBase);

    Type getType();

    enum Type{

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

        public static Type of(int typeId) {
            if (typeId >= 0 && typeId < values().length) {
                return Type.values()[typeId];
            }
            throw new IllegalArgumentException("Invalid tag id: " + typeId);
        }

        public boolean is(int typeId) {
            return this.ordinal() == typeId;
        }

        public int getId() {
            return this.ordinal();
        }
    }

}
