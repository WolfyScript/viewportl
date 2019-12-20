package me.wolfyscript.utilities.api.inventory.events;

import javax.annotation.Nonnull;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public class GuiCloseEvent extends InventoryCloseEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final String guiCluster;
    private final GuiWindow guiWindow;
    private final GuiHandler guiHandler;

    public GuiCloseEvent(String guiCluster, GuiWindow guiWindow, GuiHandler guiHandler, InventoryView transaction) {
        super(transaction);
        this.cancelled = false;
        this.guiCluster = guiCluster;
        this.guiWindow = guiWindow;
        this.guiHandler = guiHandler;
    }

    public boolean verify(GuiWindow guiWindow) {
        return guiWindow.equals(this.guiWindow);
    }

    public String getGuiCluster() {
        return guiCluster;
    }

    public GuiWindow getGuiWindow() {
        return guiWindow;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
