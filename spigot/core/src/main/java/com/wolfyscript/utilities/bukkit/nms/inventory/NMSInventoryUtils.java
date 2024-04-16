package com.wolfyscript.utilities.bukkit.nms.inventory;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.nms.Reflection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.bukkit.inventory.Inventory;

public class NMSInventoryUtils {

    // TODO: Combine these and FunctionalRecipeGenerator constants!
    private static final Class<?> CONTAINER_CLASS = Reflection.getNMS("world", "IInventory");
    private static final Class<?> RECIPE_CLASS = Reflection.getNMS("world.item.crafting", "IRecipe");
    private static final Class<?> RESOURCE_KEY_CLASS = Reflection.getNMS("resources", "MinecraftKey");
    private static final Class<?> MINECRAFT_SERVER_CLASS = Reflection.getNMS("server", "MinecraftServer");
    private static final Class<?> RECIPE_MANAGER_CLASS = Reflection.getNMS("world.item.crafting", "CraftingManager");

    private static final Method MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD;
    private static final Method MINECRAFT_SERVER_STATIC_GETTER_METHOD;
    private static final Method RECIPE_MANAGER_GET_RECIPE_METHOD;
    private static final Method CONTAINER_SET_CURRENT_RECIPE;

    private static final Class<?> CRAFT_INVENTORY_CLASS = Reflection.getOBC("inventory.CraftInventory");

    private static final Method CRAFT_INV_GET_CONTAINER;

    static {
        try {
            CONTAINER_SET_CURRENT_RECIPE = CONTAINER_CLASS.getMethod("setCurrentRecipe", RECIPE_CLASS);
            MINECRAFT_SERVER_STATIC_GETTER_METHOD = Reflection.getMethod(MINECRAFT_SERVER_CLASS, "getServer");
            MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD = Arrays.stream(MINECRAFT_SERVER_CLASS.getMethods()).filter(method -> method.getReturnType().equals(RECIPE_MANAGER_CLASS)).findFirst().orElseGet(() -> Reflection.getMethod(MINECRAFT_SERVER_CLASS, "getCraftingManager"));
            RECIPE_MANAGER_GET_RECIPE_METHOD = Reflection.getMethod(RECIPE_MANAGER_CLASS, "a", RESOURCE_KEY_CLASS);

            CRAFT_INV_GET_CONTAINER = CRAFT_INVENTORY_CLASS.getMethod("getInventory");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCurrentRecipe(Inventory inventory, NamespacedKey recipeId) {
        if (inventory == null) return;
        if (CRAFT_INVENTORY_CLASS.isInstance(inventory)) {
            try {
                Object container = CRAFT_INV_GET_CONTAINER.invoke(CRAFT_INVENTORY_CLASS.cast(inventory));
                if (CONTAINER_CLASS.isInstance(container)) {
                    Object recipeMCKey = RESOURCE_KEY_CLASS.getConstructor(String.class, String.class).newInstance(recipeId.getNamespace(), recipeId.getKey());
                    Object minecraftServer = MINECRAFT_SERVER_STATIC_GETTER_METHOD.invoke(null);
                    Object recipeManager = MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD.invoke(minecraftServer);
                    Object recipeOptional = RECIPE_MANAGER_GET_RECIPE_METHOD.invoke(recipeManager, recipeMCKey);
                    if (recipeOptional instanceof Optional<?> optional && optional.isPresent()) {
                        Object recipe = optional.get();
                        CONTAINER_SET_CURRENT_RECIPE.invoke(container, recipe);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
