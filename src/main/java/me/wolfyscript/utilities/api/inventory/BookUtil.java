/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookUtil {

    private final WolfyUtilities wolfyUtilities;
    private final Chat chat;

    public BookUtil(WolfyUtilities wolfyUtilities){
        this.wolfyUtilities = wolfyUtilities;
        this.chat = wolfyUtilities.getChat();
    }

    /**
     * Opens a book for the specified player.<br>
     * The components represent the pages. Each Component is one page.
     *
     * @param player The player to open the book for.
     * @param author The author of the book.
     * @param title The title of the book.
     * @param editable If the book can be edited.
     * @param pages The pages of the book.
     */
    public void openBook(Player player, String author, String title, boolean editable, Component... pages) {
        ItemStack itemStack = new ItemStack(editable ? Material.BOOK : Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(title);
        for (Component component : pages) {
            bookMeta.spigot().addPage(BungeeComponentSerializer.get().serialize(component));
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
    }

    public void openBook(Player player, String author, String title, boolean editable, ClickData[]... clickData) {
        ItemStack itemStack = new ItemStack(editable ? Material.BOOK : Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(title);
        for (ClickData[] d : clickData) {
            TextComponent[] textComponents = chat.getActionMessage("", player, d);
            bookMeta.spigot().addPage(textComponents);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
    }

    public void openBook(Player player, boolean editable, ClickData[]... clickData) {
        openBook(player, "WolfyUtilities", "Blank", editable, clickData);
    }
}
