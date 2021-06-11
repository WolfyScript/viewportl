package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InventoryAPI<C extends CustomCache> implements Listener {

    private final Plugin plugin;
    private final WolfyUtilities wolfyUtilities;
    private final HashMap<UUID, GuiHandler<C>> guiHandlers = new HashMap<>();
    private final HashMap<String, GuiCluster<C>> guiClusters = new HashMap<>();

    private final Class<C> customCacheClass;

    public InventoryAPI(Plugin plugin, WolfyUtilities wolfyUtilities, Class<C> customCacheClass) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = plugin;
        this.customCacheClass = customCacheClass;
        getCacheInstance();
    }

    /**
     * Register a {@link GuiCluster}
     * If there is already a GuiCluster with the same key, then it will be replaced with the new value.
     *
     * @param guiCluster The {@link GuiCluster} to register.
     */
    public void registerCluster(GuiCluster<C> guiCluster) {
        guiCluster.onInit();
        guiClusters.putIfAbsent(guiCluster.getId(), guiCluster);
    }

    /**
     * Get the {@link GuiCluster} by the key.
     *
     * @param clusterID The key of the {@link GuiCluster}.
     * @return The {@link GuiCluster} associated with the key. Null if none is found.
     */
    @Nullable
    public GuiCluster<C> getGuiCluster(String clusterID) {
        return guiClusters.get(clusterID);
    }

    public boolean hasGuiCluster(String clusterID) {
        return getGuiCluster(clusterID) != null;
    }

    public GuiWindow<C> getGuiWindow(NamespacedKey namespacedKey) {
        return getGuiCluster(namespacedKey.getNamespace()).getGuiWindow(namespacedKey.getKey());
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public void openCluster(Player player, String clusterID) {
        getGuiHandler(player).openCluster(clusterID);
    }

    public void openGui(Player player, NamespacedKey namespacedKey) {
        getGuiHandler(player).openWindow(namespacedKey);
    }

    public void removeGui(Player player) {
        if (hasGuiHandler(player)) {
            removePlayerGuiHandler(player);
        }
    }

    /**
     * Get or create the {@link GuiHandler} for this player.
     *
     * @param player The player for the GuiHandler
     * @return The GuiHandler for this player.
     */
    @NotNull
    public GuiHandler<C> getGuiHandler(Player player) {
        if (!hasGuiHandler(player)) {
            createGuiHandler(player);
        }
        return guiHandlers.get(player.getUniqueId());
    }

    private void createGuiHandler(Player player) {
        GuiHandler<C> guiHandler = new GuiHandler<>(player, wolfyUtilities, this, getCacheInstance());
        setPlayerGuiHandler(player, guiHandler);
    }

    private void setPlayerGuiHandler(Player player, GuiHandler<C> guiStudio) {
        guiHandlers.put(player.getUniqueId(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player, GuiHandler<?> guiStudio) {
        guiHandlers.remove(player.getUniqueId(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player) {
        guiHandlers.remove(player.getUniqueId());
    }

    public boolean hasGuiHandler(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null;
    }

    public boolean hasGuiHandlerAndWindow(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null && guiHandlers.get(player.getUniqueId()).getWindow() != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * This method will reset the entire GUI Buttons and re-initiates the GUIClusters afterwards.
     * Be careful when calling this method.
     * It's main purpose is to reload the GUI after the language was changed or other data changed that requires the buttons to re-initiate.
     */
    public void reset() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.closeInventory();
            removeGui(player);
        }
        guiHandlers.clear();
        guiClusters.forEach((s, guiCluster) -> {
            guiCluster.getButtons().clear();
            guiCluster.getGuiWindows().values().forEach(guiWindow -> guiWindow.buttons.clear());
        });
        guiClusters.forEach((s, cGuiCluster) -> cGuiCluster.onInit());
    }

    /**
     * Will create a new instance of the cache.
     * <br>
     * It's going to use the defined class from the constructor to create the cache.
     * <br>
     * <b>The cache requires a default constructor with no params!</b>, else if the constructor doesn't exist or other errors occur it will return null.
     *
     * @return A new instance of the cache, or null if there was an error (e.g. The cache class doesn't contain a default constructor).
     */
    public C getCacheInstance() {
        try {
            Constructor<C> constructor = this.customCacheClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    Get an globally registered Button.
    This returns an Button out of the specific namespace.
     */
    public Button<C> getButton(NamespacedKey namespacedKey) {
        if (namespacedKey == null) return null;
        return getGuiCluster(namespacedKey.getNamespace()).getButton(namespacedKey.getKey());
    }

    public void onClick(GuiHandler<C> guiHandler, GUIInventory<C> inventory, InventoryClickEvent event) {
        GuiWindow<C> guiWindow = inventory.getWindow();
        event.setCancelled(true);
        if (guiWindow == null) return;
        HashMap<Integer, Button<C>> buttons = new HashMap<>();
        if (inventory.equals(event.getClickedInventory())) {
            if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                for (Map.Entry<Integer, String> buttonEntry : guiHandler.getCustomCache().getButtons(guiWindow).entrySet()) {
                    if (event.getSlot() != buttonEntry.getKey()) {
                        Button<C> button = guiWindow.getButton(buttonEntry.getValue());
                        if (button instanceof ItemInputButton) {
                            buttons.put(buttonEntry.getKey(), button);
                            event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), inventory, buttonEntry.getKey(), event));
                        }
                    }
                }
            }
            Button<C> button = guiHandler.getButton(guiWindow, event.getSlot());
            if (button != null) {
                buttons.put(event.getSlot(), button);
                event.setCancelled(executeButton(button, guiHandler, (Player) event.getWhoClicked(), inventory, event.getSlot(), event));
            }
        } else if (!event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(false);
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                for (Map.Entry<Integer, String> buttonEntry : guiHandler.getCustomCache().getButtons(guiWindow).entrySet()) {
                    Button<C> button = guiWindow.getButton(buttonEntry.getValue());
                    if (button instanceof ItemInputButton) {
                        buttons.put(buttonEntry.getKey(), button);
                        if (executeButton(button, guiHandler, (Player) event.getWhoClicked(), inventory, buttonEntry.getKey(), event)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
        if (guiHandler.openedPreviousWindow) {
            guiHandler.openedPreviousWindow = false;
        } else if (guiHandler.getWindow() != null && guiHandler.isWindowOpen()) {
            guiWindow.update(inventory, buttons, event);
        }
    }

    public void onDrag(GuiHandler<C> guiHandler, GUIInventory<C> inventory, InventoryDragEvent event) {
        if (event.getRawSlots().parallelStream().anyMatch(rawSlot -> !Objects.equals(event.getView().getInventory(rawSlot), inventory))) {
            event.setCancelled(true);
            return;
        }
        GuiWindow<C> guiWindow = guiHandler.getWindow();
        if (guiWindow != null) {
            HashMap<Integer, Button<C>> buttons = new HashMap<>();
            for (int slot : event.getInventorySlots()) {
                Button<C> button = guiHandler.getButton(guiWindow, slot);
                if (button == null) {
                    event.setCancelled(true);
                    return;
                }
                buttons.put(slot, button);
            }
            for (Map.Entry<Integer, Button<C>> button : buttons.entrySet()) {
                event.setCancelled(executeButton(button.getValue(), guiHandler, (Player) event.getWhoClicked(), inventory, button.getKey(), event));
            }
            if (guiHandler.openedPreviousWindow) {
                guiHandler.openedPreviousWindow = false;
            } else if (guiHandler.getWindow() != null) {
                guiWindow.update(inventory, buttons, event);
            }
        }
    }

    private boolean executeButton(Button<C> button, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) {
        try {
            return button.execute(guiHandler, player, inventory, slot, event);
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Checks if the player sending the message has active chat events. If he has, it's executed!
     * It cancels the event and passes the message into the /wui command.
     * <strong>It is recommended to use the /wui command instead of typing directly into the chat.</ strong>
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreChat(AsyncPlayerChatEvent event) {
        if (hasGuiHandler(event.getPlayer())) {
            GuiHandler<C> guiHandler = getGuiHandler(event.getPlayer());
            if (guiHandler.isChatEventActive()) {
                final String message = event.getMessage();
                //Wraps normal written message into command to be executed
                Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.dispatchCommand(event.getPlayer(), "wui " + message));
                event.setCancelled(true);
            }
        }
    }

}
