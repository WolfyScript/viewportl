package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.ChatInputAction;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ChatInputButton extends DummyButton {

    private ChatInputAction action;
    private String msg = "";
    private ClickData clickData = null;

    public ChatInputButton(String id, ButtonState buttonState, String msg, ChatInputAction action) {
        super(id, buttonState);
        this.action = action;
        this.msg = msg;
    }

    public ChatInputButton(String id, ButtonState buttonState,  ChatInputAction action) {
        super(id, buttonState);
        this.action = action;
    }

    public ChatInputButton(String id, ButtonState buttonState, ClickData clickData, ChatInputAction action) {
        super(id, buttonState);
        this.action = action;
        this.clickData = clickData;
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        guiHandler.setChatInputAction(action);
        if(!msg.isEmpty()){
            guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), msg);
        }else if(clickData != null){
            guiHandler.getApi().sendActionMessage(guiHandler.getPlayer(), clickData);
        }else {
            if(guiHandler.getCurrentInv() != null){
                guiHandler.getApi().sendPlayerMessage(player, "$inventories."+guiHandler.getCurrentGuiCluster()+"."+guiHandler.getCurrentInv().getNamespace()+".items."+getId()+".message$");
            }
        }
        guiHandler.close();
        return true;
    }
}
