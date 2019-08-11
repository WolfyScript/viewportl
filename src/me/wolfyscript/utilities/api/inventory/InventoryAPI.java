package me.wolfyscript.utilities.api.inventory;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryAPI implements Listener {

    private Plugin plugin;
    private WolfyUtilities wolfyUtilities;
    private HashMap<String, GuiHandler> guiHandlers = new HashMap<>();
    private HashMap<String, GuiWindow> guiWindows;
    private HashMap<String, ItemStack[]> items;
    private HashMap<String, List<String>> itemHelpLores;

    private HashMap<NamespacedKey, Button> buttons = new HashMap<>();
    private String mainmenu;

    public InventoryAPI(Plugin plugin, WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        guiWindows = new HashMap<>();
        items = new HashMap<>();
        itemHelpLores = new HashMap<>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerGuiWindow(GuiWindow guiWindow) {
        guiWindows.put(guiWindow.getNamespace(), guiWindow);
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public GuiWindow getGuiWindow(String key) {
        return guiWindows.get(key);
    }

    public void setMainmenu(String key) {
        mainmenu = key;
    }

    public void openGui(Player player, String gui) {
        getGuiHandler(player).testForNewPlayerInstance();
        if (getGuiHandler(player).getLastInv() != null) {
            getGuiHandler(player).openLastInv();
        } else {
            getGuiHandler(player).changeToInv(gui);
        }
    }

    public void openGui(Player player) {
        openGui(player, mainmenu);
    }

    public void removeGui(Player player) {
        if (hasGuiHandler(player)) {
            removePlayerGuiHandler(player);
        }
    }

    @NotNull
    public GuiHandler getGuiHandler(Player player) {
        if (!hasGuiHandler(player)) {
            createGuiHandler(player);
        }
        return guiHandlers.get(player.getUniqueId().toString());
    }

    private void createGuiHandler(Player player) {
        setPlayerGuiStudio(player, new GuiHandler(player, wolfyUtilities));
    }

    private void setPlayerGuiStudio(Player player, GuiHandler guiStudio) {
        guiHandlers.put(player.getUniqueId().toString(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player, GuiHandler guiStudio) {
        guiHandlers.remove(player.getUniqueId().toString(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player) {
        guiHandlers.remove(player.getUniqueId().toString());
    }

    public boolean hasGuiHandler(Player player) {
        return guiHandlers.containsKey(player.getUniqueId().toString()) && guiHandlers.get(player.getUniqueId().toString()) != null;
    }

    public boolean hasGuiHandlerAndInv(Player player) {
        return guiHandlers.containsKey(player.getUniqueId().toString()) && guiHandlers.get(player.getUniqueId().toString()) != null && guiHandlers.get(player.getUniqueId().toString()).getCurrentInv() != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void reset() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.closeInventory();
            removeGui(player);
        }
        guiHandlers.clear();
        guiWindows.clear();
        items.clear();
    }

    /*
    Registers an Button globally which then can be accessed in every GUI.
    These Buttons can also be sorted into different namespaces.
    Default namespace is "none".
     */
    public void registerButton(String key, Button button) {
        registerButton("none", key, button);
    }

    /*
    Registers an Button globally which then can be accessed in every GUI.
     */
    public void registerButton(String namespace, String key, Button button) {
        registerButton(new NamespacedKey(namespace, key), button);
    }

    /*
    Registers an Button globally which then can be accessed in every GUI.
     */
    public void registerButton(NamespacedKey namespacedKey, Button button) {
        button.init(namespacedKey.getNamespace(), getWolfyUtilities());
        buttons.put(namespacedKey, button);
    }

    /*
    Get an globally registered Button.
    This returns an Button out of the default namespace.
     */
    public Button getButton(String key) {
        return getButton("none", key);
    }

    /*
    Get an globally registered Button.
    This returns an Button out of the specific namespace.
     */
    public Button getButton(String namespace, String key) {
        return getButton(new NamespacedKey(namespace, key));
    }

    /*
    Get an globally registered Button.
    This returns an Button out of the specific namespace.
     */
    public Button getButton(NamespacedKey namespacedKey) {
        return buttons.get(namespacedKey);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (hasGuiHandler((Player) event.getWhoClicked())) {
                GuiHandler guiHandler = getGuiHandler((Player) event.getWhoClicked());
                if (guiHandler.verifyInv() && guiHandler.getCurrentInv().getInventory(guiHandler).equals(event.getView().getTopInventory())) {
                    event.setCancelled(true);
                    GuiWindow guiWindow = guiHandler.getCurrentInv();

                    Button button = guiHandler.getPlayerCache().getButton(guiWindow, event.getSlot());
                    if (button != null) {
                        event.setCancelled(button.execute(guiHandler, (Player) event.getWhoClicked(), guiWindow.getInventory(guiHandler), event.getSlot(), event));
                        guiHandler.getCurrentInv().update(guiHandler);
                    } else {
                        /*
                        Deprecated!
                         */
                        String action = guiHandler.verifyItem(event.getCurrentItem());
                        if (!action.isEmpty()) {
                            GuiAction guiAction = new GuiAction(action, guiHandler, guiHandler.getCurrentInv(), event);
                            guiHandler.getCurrentInv().update(guiHandler);
                            if (!guiHandler.getCurrentInv().onAction(guiAction)) {
                                event.setCancelled(true);
                            }
                        } else {
                            GuiClick guiClick = new GuiClick(guiHandler, guiHandler.getCurrentInv(), event);
                            if (!guiHandler.getCurrentInv().onClick(guiClick)) {
                                event.setCancelled(false);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrag(InventoryDragEvent event) {
        if (event.getInventory() != null) {
            if (hasGuiHandler((Player) event.getWhoClicked())) {
                GuiHandler guiHandler = getGuiHandler((Player) event.getWhoClicked());
                if (guiHandler.verifyInv() && guiHandler.getCurrentInv().getInventory(guiHandler).equals(event.getView().getTopInventory())) {
                    GuiItemDragEvent guiItemDragEvent = new GuiItemDragEvent(guiHandler, event);
                    Bukkit.getPluginManager().callEvent(guiItemDragEvent);
                    if (guiItemDragEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage() != null) {
            if (event.getMessage().equals("[WolfyUtilities CANCELED]")) {
                event.setMessage("");
                event.setCancelled(true);
            }
        }
    }

    /*
    Checks if the player sending the message has active chat events. If he has, it's executed!
    It sets the message to a canceled string, so the following event knows to cancel it.
    This allows the message to bypass other Chat Plugins.
    Maybe I find another way to do it someday...
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreChat(AsyncPlayerChatEvent event) {
        if (event.getMessage() != null) {
            if (hasGuiHandler(event.getPlayer())) {
                GuiHandler guiHandler = getGuiHandler(event.getPlayer());
                if (guiHandler.isChatEventActive() && !event.getMessage().startsWith("wu::")) {
                    if (guiHandler.getChatInputAction() != null && !guiHandler.getChatInputAction().onChat(guiHandler, event.getPlayer(), event.getMessage(), event.getMessage().split(" "))) {
                        guiHandler.setChatInputAction(null);
                    } else if (!guiHandler.getLastInv().parseChatMessage(guiHandler.getTestChatID(), event.getMessage(), guiHandler)) {
                        guiHandler.openLastInv();
                        guiHandler.setTestChatID(-1);
                    }
                    event.setMessage("[WolfyUtilities CANCELED]");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCancel(PlayerCommandPreprocessEvent event) {
        if (hasGuiHandler(event.getPlayer())) {
            GuiHandler guiHandler = getGuiHandler(event.getPlayer());
            if (guiHandler.isChatEventActive()) {
                guiHandler.cancelChatEvent();
            }
        }
    }

    /*
        Gets the item name and lore from the set language!
         */
    @Deprecated
    public void registerItem(String namespace, String key, ItemStack itemStack) {
        String path = "items." + namespace + "." + key;
        String displayName = wolfyUtilities.getLanguageAPI().getActiveLanguage().replaceKeys("$" + path + ".name" + "$");
        String[] helpLore = wolfyUtilities.getLanguageAPI().getActiveLanguage().getConfig().get(path + ".help") != null ? wolfyUtilities.getLanguageAPI().getActiveLanguage().replaceKey(path + ".help").toArray(new String[0]) : new String[0];
        String[] normalLore = wolfyUtilities.getLanguageAPI().getActiveLanguage().getConfig().get(path + ".lore") != null ? wolfyUtilities.getLanguageAPI().getActiveLanguage().replaceKey(path + ".lore").toArray(new String[0]) : new String[0];
        registerItem(namespace, key, itemStack, displayName, helpLore, normalLore);
    }

    /*
    Registers an item, which is not connected with the languages!
     */
    @Deprecated
    public void registerItem(String namespace, String key, ItemStack itemStack, String displayName, String[] helpLore, String... normalLore) {
        items.put(namespace + ":" + key, ItemUtils.createItem(itemStack, displayName + WolfyUtilities.hideString("::" + key + "::" + Main.getMainConfig().getString("securityCode")), helpLore, normalLore));
    }

    @Deprecated
    public ItemStack[] getItem(String namespace, String id) {
        return items.getOrDefault(namespace + ":" + id, new ItemStack[]{new ItemStack(Material.STONE), new ItemStack(Material.STONE)});
    }

    @Deprecated
    public List<String> getItemHelpLore(String namespace, String id) {
        return itemHelpLores.getOrDefault(namespace + ":" + id, new ArrayList<>());
    }

    @Deprecated
    public ItemStack getItem(String namespace, String id, boolean help) {
        ItemStack[] itemStacks = getItem(namespace, id);
        if (help) {
            return itemStacks[1].clone();
        }
        return itemStacks[0].clone();
    }

}
