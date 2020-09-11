package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ButtonState {

    private String clusterID;
    private String key;
    private final ItemStack presetIcon;
    private ItemStack[] icon;
    private ButtonAction action;
    private ButtonRender buttonRender = null;

    private String displayName;
    private String[] helpLore;
    private String[] normalLore;

    public ButtonState(String key, ItemStack presetIcon) {
        this.key = key;
        this.presetIcon = presetIcon;
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonAction action) {
        this(key, presetIcon);
        this.action = action;
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonRender render) {
        this(key, presetIcon, null);
        this.buttonRender = render;
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonAction action, ButtonRender render) {
        this(key, presetIcon, action);
        this.buttonRender = render;
    }

    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, ButtonActionRender action) {
        this(key, presetIcon);
        this.action = action;
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonAction action) {
        this(key, presetIcon, action);
        ItemMeta itemMeta = this.presetIcon.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.presetIcon.setItemMeta(itemMeta);
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonRender render) {
        this(key, presetIcon);
        ItemMeta itemMeta = this.presetIcon.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.presetIcon.setItemMeta(itemMeta);
        this.buttonRender = render;
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonAction action, ButtonRender render) {
        this(key, presetIcon, customModelData, action);
        this.buttonRender = render;
    }

    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonActionRender action) {
        this(key, presetIcon, action);
        ItemMeta itemMeta = this.presetIcon.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.presetIcon.setItemMeta(itemMeta);
    }

    public ButtonState(String key, Material presetIcon) {
        this(key, new ItemStack(presetIcon), null);
    }

    public ButtonState(String key, Material presetIcon, ButtonAction action) {
        this(key, new ItemStack(presetIcon), action);
    }

    public ButtonState(String key, Material presetIcon, ButtonRender render) {
        this(key, presetIcon, null, render);
    }

    public ButtonState(String key, Material presetIcon, ButtonAction action, ButtonRender render) {
        this(key, presetIcon, action);
        this.buttonRender = render;
    }

    @Deprecated
    public ButtonState(String key, Material presetIcon, ButtonActionRender action) {
        this(key, new ItemStack(presetIcon), action);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonAction action) {
        this(key, new ItemStack(presetIcon), customModelData, action);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonRender render) {
        this(key, presetIcon, customModelData, null, render);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonAction action, ButtonRender render) {
        this(key, presetIcon, customModelData, action);
        this.buttonRender = render;
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonActionRender action) {
        this(key, new ItemStack(presetIcon), customModelData, action);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, ButtonAction action) {
        this.action = action;
        this.presetIcon = presetIcon;
        this.clusterID = clusterID;
        this.key = key;
    }

    public ButtonState(String clusterID, String key, @NotNull ItemStack presetIcon, int customModelData, ButtonAction action) {
        this.action = action;
        this.presetIcon = presetIcon;
        ItemMeta itemMeta = this.presetIcon.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.presetIcon.setItemMeta(itemMeta);
        this.clusterID = clusterID;
        this.key = key;
    }

    public ButtonState(String clusterID, String key, Material presetIcon, ButtonAction action) {
        this(clusterID, key, new ItemStack(presetIcon), action);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, int customModelData, ButtonAction action) {
        this(clusterID, key, new ItemStack(presetIcon), customModelData, action);
    }

    /*
    Not linked to language file
     */
    public ButtonState(ItemStack presetIcon, String displayName, String[] helpLore, String[] normalLore, ButtonAction action) {
        this.action = action;
        this.presetIcon = presetIcon;
        this.icon = ItemUtils.createItem(presetIcon, displayName, helpLore, normalLore);
    }

    public ButtonState(Material presetIcon, String displayName, String[] helpLore, String[] normalLore, ButtonAction action) {
        this(new ItemStack(presetIcon), displayName, helpLore, normalLore, action);
    }

    //------------------------------------------------

    public void init(GuiWindow window) {
        if (key != null && !key.isEmpty()) {
            String path = "inventories." + window.getClusterID() + "." + window.getNamespace() + ".items." + key;
            if (clusterID != null && !clusterID.isEmpty()) {
                path = "inventories." + clusterID + ".global_items." + key;
            }
            displayName = window.getAPI().getLanguageAPI().replaceKeys("$" + path + ".name" + "$");
            List<String> help = window.getAPI().getLanguageAPI().replaceKey(path + ".help");
            List<String> normal = window.getAPI().getLanguageAPI().replaceKey(path + ".lore");
            helpLore = !help.isEmpty() ? help.toArray(new String[0]) : new String[0];
            normalLore = !normal.isEmpty() ? normal.toArray(new String[0]) : new String[0];
            this.icon = ItemUtils.createItem(presetIcon, displayName, helpLore, normalLore);
        }
    }

    public void init(String clusterID, WolfyUtilities api) {
        if (key != null && !key.isEmpty()) {
            String path = "inventories." + clusterID + ".global_items." + key;
            displayName = api.getLanguageAPI().replaceKeys("$" + path + ".name" + "$");
            List<String> help = api.getLanguageAPI().replaceKey(path + ".help");
            List<String> normal = api.getLanguageAPI().replaceKey(path + ".lore");
            helpLore = !help.isEmpty() ? help.toArray(new String[0]) : new String[0];
            normalLore = !normal.isEmpty() ? normal.toArray(new String[0]) : new String[0];
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

    public ButtonState setRenderAction(ButtonRender renderAction) {
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
