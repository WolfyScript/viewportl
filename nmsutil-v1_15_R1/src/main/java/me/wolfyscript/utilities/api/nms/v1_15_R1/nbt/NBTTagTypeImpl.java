package me.wolfyscript.utilities.api.nms.v1_15_R1.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTBase;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NBTTagTypeImpl<T extends NBTBase> implements NBTTagType<T> {

    protected final Type type;
    private final Function<Object, T> function;

    public NBTTagTypeImpl(Type type, Function<Object, T> nbtBase){
        this.type = type;
        this.function = nbtBase;
    }

    public T get(@Nullable Object nbtBase){
        if(nbtBase instanceof net.minecraft.server.v1_15_R1.NBTBase && type.is(((net.minecraft.server.v1_15_R1.NBTBase) nbtBase).getTypeId())){
            return function.apply(nbtBase);
        }
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }

}
