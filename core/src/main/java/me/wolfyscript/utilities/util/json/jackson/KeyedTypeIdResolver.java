package me.wolfyscript.utilities.util.json.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import me.wolfyscript.utilities.util.ClassRegistry;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class KeyedTypeIdResolver extends TypeIdResolverBase {

    private static final Map<Class<?>, Registry<?>> TYPE_REGISTRIES = new HashMap<>();
    private static final Map<Class<?>, ClassRegistry<?>> TYPE_CLASS_REGISTRIES = new HashMap<>();
    private JavaType superType;

    public static <T extends Keyed> void registerTypeRegistry(Class<T> type, Registry<T> registry) {
        TYPE_REGISTRIES.putIfAbsent(type, registry);
    }

    public static <T extends Keyed> void registerTypeRegistry(Class<T> type, ClassRegistry<T> registry) {
        TYPE_CLASS_REGISTRIES.putIfAbsent(type, registry);
    }

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return getKey(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> aClass) {
        return getKey(value);
    }

    private String getKey(Object value) {
        if (value instanceof Keyed keyed) {
            return keyed.getNamespacedKey().toString();
        }
        throw new IllegalArgumentException(String.format("Object %s is not of type Keyed!", value.getClass().getName()));
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<?> clazz = getTypeClass(NamespacedKey.of(id));
        return clazz != null ? context.constructSpecializedType(superType, clazz) : TypeFactory.unknownType();
    }

    @Nullable
    protected Class<?> getTypeClass(NamespacedKey key) {
        if (key != null) {
            ClassRegistry<?> classRegistry = TYPE_CLASS_REGISTRIES.get(superType.getRawClass());
            if (classRegistry != null) {
                return classRegistry.get(key);
            }
            Registry<?> registry = TYPE_REGISTRIES.get(superType.getRawClass());
            if (registry != null) {
                var object = registry.get(key);
                if (object != null) {
                    return object.getClass();
                }
            }
        }
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
