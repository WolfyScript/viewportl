package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryAPI implements Listener {

    private Plugin plugin;
    private WolfyUtilities wolfyUtilities;
    private HashMap<String, GuiHandler> guiHandlers = new HashMap<>();
    private HashMap<String, GuiWindow> guiWindows;
    private HashMap<String, ItemStack> items;
    private HashMap<String, List<String>> itemHelpLores;
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

    /*
        Gets the item name and lore from the set language!
         */
    public void registerItem(String namespace, String key, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String path = "items." + namespace + "." + key;
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', wolfyUtilities.getLanguageAPI().getActiveLanguage().replaceKeys("$" + path + ".name" + "$")) + WolfyUtilities.hideString("::" + key + "::" + Main.getMainUtil().getConfigAPI().getConfig("main_config").getString("securityCode")));
        List<String> loreFirst = new ArrayList<>();
        if (wolfyUtilities.getLanguageAPI().getActiveLanguage().getConfig().contains(path + ".lore")) {
            for (String row : wolfyUtilities.getLanguageAPI().getActiveLanguage().replaceKey(path + ".lore")) {
                if (!row.isEmpty()) {
                    loreFirst.add(row.equalsIgnoreCase("<empty>") ? "" : ChatColor.translateAlternateColorCodes('&', row));
                }
            }
        }
        if (loreFirst.size() > 0)
            itemMeta.setLore(loreFirst);

        List<String> lore = new ArrayList<>();
        if (wolfyUtilities.getLanguageAPI().getActiveLanguage().getConfig().contains(path + ".help")) {
            for (String row : wolfyUtilities.getLanguageAPI().getActiveLanguage().replaceKey(path + ".help")) {
                if (!row.isEmpty()) {
                    lore.add(row.equalsIgnoreCase("<empty>") ? "" : ChatColor.translateAlternateColorCodes('&', row));
                }
            }
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemStack.setItemMeta(itemMeta);
        items.put(namespace + ":" + key, itemStack);
        if (lore.size() > 0) {
            itemHelpLores.put(namespace + ":" + key, lore);
        }
    }

    /*
    Registers an item, which is not connected with the languages!
     */
    public void registerItem(String namespace, String key, ItemStack itemStack, String displayName, String[] helpLore, String... normalLore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName + WolfyUtilities.hideString("::" + key + "::" + wolfyUtilities.getConfigAPI().getConfig("main_config").getString("securityCode"))));
        List<String> loreFirst = new ArrayList<>();
        if (normalLore != null && normalLore.length > 0) {
            for (String row : normalLore) {
                if (!row.isEmpty()) {
                    loreFirst.add(row.equalsIgnoreCase("<empty>") ? "" : ChatColor.translateAlternateColorCodes('&', row));
                }
            }
        }
        if (loreFirst.size() > 0)
            itemMeta.setLore(loreFirst);

        List<String> lore = new ArrayList<>();
        if (helpLore != null && helpLore.length > 0) {
            for (String row : helpLore) {
                if (!row.isEmpty()) {
                    lore.add(row.equalsIgnoreCase("<empty>") ? "" : ChatColor.translateAlternateColorCodes('&', row));
                }
            }
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemStack.setItemMeta(itemMeta);
        items.put(namespace + ":" + key, itemStack);
        if (lore.size() > 0) {
            itemHelpLores.put(namespace + ":" + key, lore);
        }
    }

    public ItemStack getItem(String namespace, String id) {
        return items.getOrDefault(namespace + ":" + id, new ItemStack(Material.STONE));
    }

    public List<String> getItemHelpLore(String namespace, String id) {
        return itemHelpLores.getOrDefault(namespace + ":" + id, new ArrayList<>());
    }

    public ItemStack getItem(String namespace, String id, boolean help) {
        ItemStack itemStack = getItem(namespace, id);
        if (help) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (itemMeta.hasLore()) {
                lore = itemMeta.getLore();
            }
            lore.addAll(getItemHelpLore(namespace, id));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return itemStack;
    }

    public GuiWindow getGuiWindow(String key) {
        return guiWindows.get(key);
    }

    public void setMainmenu(String key) {
        mainmenu = key;
    }

    public void openGui(Player player) {
        if (hasGuiHandler(player)) {
            getGuiHandler(player).testForNewPlayerInstance();
            getGuiHandler(player).openLastInv();
        } else {
            createGuiHandler(player);
            getGuiHandler(player).changeToInv(mainmenu);
        }
    }

    public void removeGui(Player player) {
        if (hasGuiHandler(player)) {
            removePlayerGuiHandler(player);
        }
    }

    public GuiHandler getGuiHandler(Player player) {
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
        return guiHandlers.containsKey(player.getUniqueId().toString()) && guiHandlers.get(player.getUniqueId().toString()) != null && guiHandlers.get(player.getUniqueId().toString()).getCurrentInv() != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void reset(){
        for(Player player : plugin.getServer().getOnlinePlayers()){
            player.closeInventory();
            removeGui(player);
        }
        guiHandlers.clear();
        guiWindows.clear();
        items.clear();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (hasGuiHandler((Player) event.getWhoClicked())) {
                GuiHandler guiHandler = getGuiHandler((Player) event.getWhoClicked());
                if (guiHandler.verifyInv() && guiHandler.getCurrentInv().getInventory(guiHandler).equals(event.getView().getTopInventory())) {
                    event.setCancelled(true);
                    String action = guiHandler.verifyItem(event.getCurrentItem());
                    if (!action.isEmpty()) {
                        guiHandler.getCurrentInv().update(guiHandler);
                        GuiAction guiAction = new GuiAction(action, guiHandler, guiHandler.getCurrentInv(), event);
                        if(!guiHandler.getCurrentInv().onAction(guiAction)){
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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (hasGuiHandler(event.getPlayer())) {
            GuiHandler guiHandler = getGuiHandler(event.getPlayer());
            if (guiHandler.isChatEventActive()) {
                if (!guiHandler.getLastInv().parseChatMessage(guiHandler.getTestChatID(), event.getMessage(), guiHandler)) {
                    guiHandler.openLastInv();
                    guiHandler.setTestChatID(-1);
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

}
