package me.wolfyscript.utilities.api.utils;

import me.wolfyscript.utilities.main.Main;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * @deprecated not finished! No posible solution to convert Json to Entity found yet.
 */
@Deprecated
public class EnityUtils {

    private static final Class<?> craftEntityClazz = Reflection.getOBC("entity.CraftEntity");
    private static final Class<?> nmsEntityClazz = Reflection.getNMS("Entity");
    private static final Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");

    public String convertEntityToJson(Entity entity) {

        // Gets the method to save the CraftEntity
        Method saveToNBTMethod = Reflection.getMethod(craftEntityClazz, "save", ItemStack.class);
        Object entityAsJsonObject;
        try {
            entityAsJsonObject = saveToNBTMethod.invoke(entity);
        } catch (Throwable t) {
            Main.getMainUtil().sendConsoleMessage("failed to serialize Entity to nms json!");
            Main.getMainUtil().sendConsoleMessage(t.toString());
            for (StackTraceElement element : t.getStackTrace()) {
                Main.getMainUtil().sendConsoleMessage(element.toString());
            }
            return null;
        }
        return entityAsJsonObject.toString();
    }

    @Deprecated
    public Entity convertJsonToEntity(String json) {
        return null;
    }
}
