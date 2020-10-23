package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.ChatInputAction;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonAction;
import me.wolfyscript.utilities.api.inventory.button.ButtonRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 *
 */
public class ChatInputButton extends DummyButton {

    private final ChatInputAction action;
    private String msg = "";
    private ClickData clickData = null;

    public ChatInputButton(String id, ButtonState buttonState, String msg, ChatInputAction action) {
        super(id, buttonState);
        this.action = action;
        this.msg = msg;
    }

    public ChatInputButton(String id, ItemStack itemStack, String msg, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack), msg, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction btnAction, String msg, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, btnAction), msg, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonRender render, String msg, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, render), msg, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction btnAction, ButtonRender render, String msg, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, btnAction, render), msg, action);
    }

    public ChatInputButton(String id, ButtonState buttonState, ChatInputAction action) {
        super(id, buttonState);
        this.action = action;
    }

    public ChatInputButton(String id, ItemStack itemStack, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack), action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction btnAction, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, btnAction), action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonRender render, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, render), action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction btnAction, ButtonRender render, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, btnAction, render), action);
    }

    public ChatInputButton(String id, Material material, ChatInputAction action) {
        this(id, new ButtonState(id, material), action);
    }

    public ChatInputButton(String id, Material material, ButtonAction btnAction, ChatInputAction action) {
        this(id, new ButtonState(id, material, btnAction), action);
    }

    public ChatInputButton(String id, Material material, ButtonRender render, ChatInputAction action) {
        this(id, new ButtonState(id, material, render), action);
    }

    public ChatInputButton(String id, Material material, ButtonAction btnAction, ButtonRender render, ChatInputAction action) {
        this(id, new ButtonState(id, material, btnAction, render), action);
    }

    public ChatInputButton(String id, ButtonState buttonState, ClickData clickData, ChatInputAction action) {
        super(id, buttonState);
        this.action = action;
        this.clickData = clickData;
    }

    public ChatInputButton(String id, ItemStack itemStack, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack), clickData, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonRender render, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, render), clickData, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction btnAction, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, btnAction), clickData, action);
    }

    public ChatInputButton(String id, ItemStack itemStack, ButtonAction btnAction, ButtonRender render, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, itemStack, btnAction, render), clickData, action);
    }

    public ChatInputButton(String id, Material material, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, material), clickData, action);
    }

    public ChatInputButton(String id, Material material, ButtonRender render, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, material, render), clickData, action);
    }

    public ChatInputButton(String id, Material material, ButtonAction btnAction, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, material, btnAction), clickData, action);
    }

    public ChatInputButton(String id, Material material, ButtonAction btnAction, ButtonRender render, ClickData clickData, ChatInputAction action) {
        this(id, new ButtonState(id, material, btnAction, render), clickData, action);
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        guiHandler.setChatInputAction(action);
        /*
        WUPlugin wuPlugin = WUPlugin.getInstance();
        InputButtonMessage message = new InputButtonMessage(getId(), "Test Message!");
        wuPlugin.getPacketHandler().sendTo(player, message);
         */
        if (!msg.isEmpty()) {
            guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), msg);
        } else if (clickData != null) {
            guiHandler.getApi().sendActionMessage(guiHandler.getPlayer(), clickData);
        } else {
            if (guiHandler.getCurrentInv() != null) {
                guiHandler.getApi().sendPlayerMessage(player, "$inventories." + guiHandler.getCurrentGuiCluster() + "." + guiHandler.getCurrentInv().getNamespace() + ".items." + getId() + ".message$");
            }
        }
        guiHandler.close();
        return true;
    }
}
