package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class ButtonState {

    private Material iconType;
    private NamespacedKey icon;
    private ButtonAction action;

    public ButtonState(String namespace, String key, Material type, ButtonAction action){
        this.icon = new NamespacedKey(namespace, key);
        this.action = action;
        this.iconType = type;
    }

    public void init(WolfyUtilities api){
        api.getInventoryAPI().registerItem(icon.getNamespace(), icon.getKey(), iconType);
    }

    public NamespacedKey getIcon() {
        return icon;
    }

    public ButtonAction getAction() {
        return action;
    }
}
