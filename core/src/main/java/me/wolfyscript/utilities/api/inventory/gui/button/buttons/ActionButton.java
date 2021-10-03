package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.*;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Button that executes the action method and renders the item manipulated via the render method of the ButtonState.
 *
 * action - these methods are executed when the button is clicked.
 *
 * render - these methods are executed when the button is rendered in one of
 * the possible render methods: {@link GuiWindow#onUpdateAsync(GuiUpdate)}, {@link GuiWindow#onUpdateSync(GuiUpdate)}
 *
 * You can set them directly using the constructor and the id of the button is passed into the ButtonState.
 * If the ButtonState requires another key (e.g. when using global item names from lang file) you need to create an ButtonState instance and use
 * {@link ActionButton#ActionButton(String, ButtonState)}
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ActionButton<C extends CustomCache> extends Button<C> {

    private final String id;
    private final ButtonType type;
    private final ButtonState<C> state;

    protected ActionButton(String id, ButtonType type, ButtonState<C> state) {
        super(id, type);
        this.id = id;
        this.type = type;
        this.state = state;
    }

    public ActionButton(String id, ButtonState<C> state) {
        this(id, ButtonType.NORMAL, state);
    }

    public ActionButton(String id, ItemStack itemStack) {
        this(id, new ButtonState<>(id, itemStack));
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonAction<C> action) {
        this(id, itemStack, action, null);
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonRender<C> render) {
        this(id, itemStack, null, render);
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(id, new ButtonState<>(id, itemStack, action, render));
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonRender<C> render, @Nullable ButtonPreRender<C> preRender) {
        this(id, new ButtonState<>(id, itemStack, action, postAction, preRender, render));
    }

    public ActionButton(String id, Material material) {
        this(id, new ButtonState<>(id, material));
    }

    public ActionButton(String id, Material material, @Nullable ButtonAction<C> action) {
        this(id, material, action, null);
    }

    public ActionButton(String id, Material material, @Nullable ButtonRender<C> render) {
        this(id, material, null, render);
    }

    public ActionButton(String id, Material material, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(id, new ButtonState<>(id, material, action, render));
    }

    public ActionButton(String id, Material material, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonRender<C> render, @Nullable ButtonPreRender<C> preRender) {
        this(id, new ButtonState<>(id, material, action, postAction, preRender, render));
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        state.init(guiWindow);
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        state.init(guiCluster);
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        if (!type.equals(ButtonType.DUMMY) && state.getAction() != null) {
            return state.getAction().execute(guiHandler.getCustomCache(), guiHandler, player, inventory, this, slot, event);
        }
        return true;
    }

    @Override
    public void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {
        if (state.getPostAction() != null) {
            state.getPostAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, event);
        }
    }

    @Override
    public void preRender(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean help) {
        if (state.getPrepareRender() != null) {
            state.getPrepareRender().prepare(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, help);
        }
    }

    @Override
    public void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) throws IOException {
        applyItem(guiHandler, player, guiInventory, inventory, state, slot, help);
    }

    @Override
    public ButtonType getType() {
        return type;
    }

    public ButtonState<C> getState() {
        return state;
    }

    @Override
    public String getId() {
        return id;
    }
}
