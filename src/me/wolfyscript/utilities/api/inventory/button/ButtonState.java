package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class ButtonState {

    private String namespace;
    private String key;
    private ItemStack presetIcon;
    private ItemStack[] icon;
    private ButtonAction action;

    private String displayName;
    private String[] helpLore;
    private String[] normalLore;

    public ButtonState(String key, ItemStack presetIcon, ButtonAction action){
        this.key = key;
        this.presetIcon = presetIcon;
        this.action = action;
    }

    public ButtonState(String namespace, String key, ItemStack presetIcon, ButtonAction action){
        this.action = action;
        this.presetIcon = presetIcon;
        this.namespace = namespace;
        this.key = key;
    }

    public ButtonState(ItemStack presetIcon, String displayName, String[] helpLore, String[] normalLore, ButtonAction action){
        this.action = action;
        this.presetIcon = presetIcon;
        this.icon = ItemUtils.createItem(presetIcon, displayName, helpLore, normalLore);
    }

    public void init(GuiWindow window, Button button){
        init(button, window.getNamespace(), window.getAPI());
    }

    public void init(Button button, String windowKey, WolfyUtilities api){
        if(key != null && key.isEmpty()){
            String path = "items." + (namespace == null || namespace.isEmpty() ? windowKey : (windowKey + namespace)) + "." + key;
            displayName = api.getLanguageAPI().getActiveLanguage().replaceKeys("$" + path + ".name" + "$");
            helpLore = api.getLanguageAPI().getActiveLanguage().getConfig().get(path + ".help") != null ? api.getLanguageAPI().getActiveLanguage().replaceKey(path + ".help").toArray(new String[0]) : new String[0];
            normalLore = api.getLanguageAPI().getActiveLanguage().getConfig().get(path + ".lore") != null ? api.getLanguageAPI().getActiveLanguage().replaceKey(path + ".lore").toArray(new String[0]) : new String[0];
        }
        this.icon = ItemUtils.createItem(presetIcon, displayName+ WolfyUtilities.hideString("::"+button.getId()), helpLore, normalLore);
    }

    public ItemStack getIcon(boolean help) {
        if(help){
            return icon[1];
        }
        return icon[0];
    }

    public ButtonAction getAction() {
        return action;
    }

    public void setAction(ButtonAction action) {
        this.action = action;
    }

    public static ItemStack createItem(){
        return null;
    }
}
