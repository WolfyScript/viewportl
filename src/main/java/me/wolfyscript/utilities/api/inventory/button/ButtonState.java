package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ButtonState {

    private String clusterID;
    private String key;
    private ItemStack presetIcon;
    private ItemStack[] icon;
    private ButtonAction action;
    private ButtonRender buttonRender = null;

    private String displayName;
    private String[] helpLore;
    private String[] normalLore;

    public ButtonState(String key, ItemStack presetIcon, ButtonAction action) {
        this.key = key;
        this.presetIcon = presetIcon;
        this.action = action;
    }

    public ButtonState(String key, Material presetIcon, ButtonAction action) {
        this(key, new ItemStack(presetIcon), action);
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonActionRender action) {
        this.key = key;
        this.presetIcon = presetIcon;
        this.action = action;
    }

    public ButtonState(String key, Material presetIcon, ButtonActionRender action) {
        this(key, new ItemStack(presetIcon), action);
    }

    public ButtonState(String key, ItemStack presetIcon) {
        this(key, presetIcon, null);
    }

    public ButtonState(String key, Material presetIcon) {
        this(key, new ItemStack(presetIcon), null);
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonRender render) {
        this(key, presetIcon, null);
        setRenderAction(render);
    }

    public ButtonState(String key, Material presetIcon, ButtonRender render) {
        this(key, new ItemStack(presetIcon), null);
        setRenderAction(render);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, ButtonAction action) {
        this.action = action;
        this.presetIcon = presetIcon;
        this.clusterID = clusterID;
        this.key = key;
    }

    public ButtonState(String clusterID, String key, Material presetIcon, ButtonAction action) {
        this(clusterID, key, new ItemStack(presetIcon), action);
    }

    public ButtonState(ItemStack presetIcon, String displayName, String[] helpLore, String[] normalLore, ButtonAction action) {
        this.action = action;
        this.presetIcon = presetIcon;
        this.icon = ItemUtils.createItem(presetIcon, displayName, helpLore, normalLore);
    }

    public ButtonState(Material presetIcon, String displayName, String[] helpLore, String[] normalLore, ButtonAction action) {
        this(new ItemStack(presetIcon), displayName, helpLore, normalLore, action);
    }

    public void init(GuiWindow window) {
        if (key != null && !key.isEmpty()) {
            String path = "inventories."+ window.getClusterID() + "." + window.getNamespace() + ".items." + key;
            if(clusterID != null && !clusterID.isEmpty()){
                path = "inventories."+ clusterID + ".global_items." + key;
            }
            displayName = window.getAPI().getLanguageAPI().getActiveLanguage().replaceKeys("$" + path + ".name" + "$");
            helpLore = window.getAPI().getLanguageAPI().getActiveLanguage().getConfig().get(path + ".help") != null ? window.getAPI().getLanguageAPI().getActiveLanguage().replaceKey(path + ".help").toArray(new String[0]) : new String[0];
            normalLore = window.getAPI().getLanguageAPI().getActiveLanguage().getConfig().get(path + ".lore") != null ? window.getAPI().getLanguageAPI().getActiveLanguage().replaceKey(path + ".lore").toArray(new String[0]) : new String[0];
            this.icon = ItemUtils.createItem(presetIcon, displayName, helpLore, normalLore);
        }
    }

    public void init(String clusterID, WolfyUtilities api) {
        if (key != null && !key.isEmpty()) {
            String path = "inventories."+ clusterID + ".global_items." + key;
            displayName = api.getLanguageAPI().getActiveLanguage().replaceKeys("$" + path + ".name" + "$");
            helpLore = api.getLanguageAPI().getActiveLanguage().getConfig().get(path + ".help") != null ? api.getLanguageAPI().getActiveLanguage().replaceKey(path + ".help").toArray(new String[0]) : new String[0];
            normalLore = api.getLanguageAPI().getActiveLanguage().getConfig().get(path + ".lore") != null ? api.getLanguageAPI().getActiveLanguage().replaceKey(path + ".lore").toArray(new String[0]) : new String[0];
        }
        this.icon = ItemUtils.createItem(presetIcon, displayName, helpLore, normalLore);
    }

    public ItemStack getIcon(boolean help) {
        if (help) {
            return icon[1].clone();
        }
        return icon[0].clone();
    }

    public ButtonAction getAction() {
        return action;
    }

    public ButtonState setAction(ButtonAction action) {
        this.action = action;
        return this;
    }

    public ButtonState setRenderAction(ButtonRender renderAction){
        this.buttonRender = renderAction;
        return this;
    }

    public static ItemStack createItem() {
        return null;
    }

    public ButtonRender getRenderAction() {
        return buttonRender;
    }
}
