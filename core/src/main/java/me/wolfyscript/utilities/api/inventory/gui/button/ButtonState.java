package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * ButtonState represents the state an Button can be in.
 * It contains the ItemStack and language keys required to render the correct Item.
 * <p>
 * The rendering can be manipulated using the render method that returns the ItemStack that will be rendered.
 * <p>
 * To execute code on a Button click you need to use the action method, which is called each time the button is clicked.
 */
public class ButtonState<C extends CustomCache> {

    private String clusterID;
    private String key;
    private final ItemStack presetIcon;
    private ItemStack icon;
    private ButtonAction<C> action;
    private ButtonRender<C> buttonRender;
    private ButtonPrepareRender<C> prepareRender;

    private String displayName;
    private String[] normalLore;

    public ButtonState(String key, ItemStack presetIcon) {
        this(key, presetIcon, null, null);
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonAction<C> action) {
        this(key, presetIcon, action, null);
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonRender<C> render) {
        this(key, presetIcon, null, render);
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonAction<C> action, ButtonRender<C> render) {
        this(key, presetIcon, action, null, render);
    }

    public ButtonState(String key, ItemStack presetIcon, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this.key = key;
        this.presetIcon = presetIcon;
        this.action = action;
        this.prepareRender = prepareRender;
        this.buttonRender = render;
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData) {
        this(key, presetIcon, customModelData, null, null);
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonAction<C> action) {
        this(key, presetIcon, customModelData, action, null);
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonRender<C> render) {
        this(key, presetIcon, customModelData, null, render);
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonAction<C> action, ButtonRender<C> render) {
        this(key, presetIcon, customModelData, action, null, render);
    }

    public ButtonState(String key, ItemStack presetIcon, int customModelData, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this(key, presetIcon, action, prepareRender, render);
        ItemMeta itemMeta = this.presetIcon.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.presetIcon.setItemMeta(itemMeta);
    }

    /**
     * Constructors with Key and, Materials instead of ItemStacks
     */
    public ButtonState(String key, Material presetIcon) {
        this(key, presetIcon, null, null);
    }

    public ButtonState(String key, Material presetIcon, ButtonAction<C> action) {
        this(key, presetIcon, action, null);
    }

    public ButtonState(String key, Material presetIcon, ButtonRender<C> render) {
        this(key, presetIcon, null, render);
    }

    public ButtonState(String key, Material presetIcon, ButtonAction<C> action, ButtonRender<C> render) {
        this(key, new ItemStack(presetIcon), action, render);
    }

    public ButtonState(String key, Material presetIcon, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this(key, new ItemStack(presetIcon), action, prepareRender, render);
    }

    public ButtonState(String key, Material presetIcon, int customModelData) {
        this(key, presetIcon, customModelData, null, null);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonAction<C> action) {
        this(key, presetIcon, customModelData, action, null);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonRender<C> render) {
        this(key, presetIcon, customModelData, null, render);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonAction<C> action, ButtonRender<C> render) {
        this(key, new ItemStack(presetIcon), customModelData, action, render);
    }

    public ButtonState(String key, Material presetIcon, int customModelData, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this(key, new ItemStack(presetIcon), customModelData, action, prepareRender, render);
    }

    /**
     * Constructors with ClusterID, Key and ItemStacks
     */

    public ButtonState(String clusterID, String key, ItemStack presetIcon) {
        this(clusterID, key, presetIcon, null, null);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, ButtonRender<C> render) {
        this(clusterID, key, presetIcon, null, render);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, ButtonAction<C> action) {
        this(clusterID, key, presetIcon, action, null);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, ButtonAction<C> action, ButtonRender<C> render) {
        this(clusterID, key, presetIcon, action, null, render);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this.action = action;
        this.prepareRender = prepareRender;
        this.buttonRender = render;
        this.presetIcon = presetIcon;
        this.clusterID = clusterID;
        this.key = key;
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, int customModelData) {
        this(clusterID, key, presetIcon, customModelData, null, null);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, int customModelData, ButtonRender<C> render) {
        this(clusterID, key, presetIcon, customModelData, null, render);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, int customModelData, ButtonAction<C> action) {
        this(clusterID, key, presetIcon, customModelData, action, null);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, int customModelData, ButtonAction<C> action, ButtonRender<C> render) {
        this(clusterID, key, presetIcon, customModelData, action, null, render);
    }

    public ButtonState(String clusterID, String key, ItemStack presetIcon, int customModelData, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this.action = action;
        this.prepareRender = prepareRender;
        this.buttonRender = render;
        this.presetIcon = presetIcon;
        ItemMeta itemMeta = this.presetIcon.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.presetIcon.setItemMeta(itemMeta);
        this.clusterID = clusterID;
        this.key = key;
    }

    /**
     * Constructors with ClusterID, Key and Materials
     */

    public ButtonState(String clusterID, String key, Material presetIcon) {
        this(clusterID, key, presetIcon, null, null);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, ButtonRender<C> render) {
        this(clusterID, key, presetIcon, null, render);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, ButtonAction<C> action) {
        this(clusterID, key, presetIcon, action, null);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, ButtonAction<C> action, ButtonRender<C> render) {
        this(clusterID, key, new ItemStack(presetIcon), action, render);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this(clusterID, key, new ItemStack(presetIcon), action, prepareRender, render);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, int customModelData) {
        this(clusterID, key, presetIcon, customModelData, null, null);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, int customModelData, ButtonRender<C> render) {
        this(clusterID, key, presetIcon, customModelData, null, render);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, int customModelData, ButtonAction<C> action) {
        this(clusterID, key, presetIcon, customModelData, action, null);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, int customModelData, ButtonAction<C> action, ButtonRender<C> render) {
        this(clusterID, key, new ItemStack(presetIcon), customModelData, action, render);
    }

    public ButtonState(String clusterID, String key, Material presetIcon, int customModelData, ButtonAction<C> action, ButtonPrepareRender<C> prepareRender, ButtonRender<C> render) {
        this(clusterID, key, new ItemStack(presetIcon), customModelData, action, prepareRender, render);
    }

    /*
    Not linked to language file
     */
    public ButtonState(ItemStack presetIcon, String displayName, String[] normalLore, ButtonAction<C> action) {
        this.action = action;
        this.presetIcon = presetIcon;
        this.icon = ItemUtils.createItem(presetIcon, displayName, normalLore);
    }

    public ButtonState(Material presetIcon, String displayName, String[] normalLore, ButtonAction<C> action) {
        this(new ItemStack(presetIcon), displayName, normalLore, action);
    }

    //------------------------------------------------

    public void init(String clusterID, WolfyUtilities api) {
        if (key != null && !key.isEmpty()) {
            String path = "inventories." + clusterID + ".global_items." + key;
            displayName = api.getLanguageAPI().replaceKeys("$" + path + ".name" + "$");
            List<String> normal = api.getLanguageAPI().replaceKey(path + ".lore");
            normalLore = !normal.isEmpty() ? normal.toArray(new String[0]) : new String[0];
            this.icon = ItemUtils.createItem(presetIcon, displayName, normalLore);
        }
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    public void init(GuiWindow<C> window) {
        if (key != null && !key.isEmpty()) {
            String path = "inventories." + window.getCluster().getId() + "." + window.getNamespacedKey().getKey() + ".items." + key;
            if (clusterID != null && !clusterID.isEmpty()) {
                path = "inventories." + clusterID + ".global_items." + key;
            }
            displayName = window.getAPI().getLanguageAPI().replaceKeys("$" + path + ".name" + "$");
            List<String> normal = window.getAPI().getLanguageAPI().replaceKey(path + ".lore");
            normalLore = !normal.isEmpty() ? normal.toArray(new String[0]) : new String[0];
            this.icon = ItemUtils.createItem(presetIcon, displayName, normalLore);
        }
    }

    public ButtonAction<C> getAction() {
        return action;
    }

    public ButtonState<C> setAction(ButtonAction<C> action) {
        this.action = action;
        return this;
    }

    public ButtonRender<C> getRenderAction() {
        return buttonRender;
    }

    public ButtonState<C> setRenderAction(ButtonRender<C> renderAction) {
        this.buttonRender = renderAction;
        return this;
    }

    public ButtonPrepareRender<C> getPrepareRender() {
        return prepareRender;
    }

    public void setPrepareRender(ButtonPrepareRender<C> prepareRender) {
        this.prepareRender = prepareRender;
    }
}
