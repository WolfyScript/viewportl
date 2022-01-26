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

package me.wolfyscript.utilities.compatibility.plugins;

import me.wolfyscript.utilities.compatibility.PluginIntegration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public interface PlaceholderAPIIntegration extends PluginIntegration {

    String NAME = "PlaceholderAPI";

    @NotNull String setPlaceholders(OfflinePlayer player, @NotNull String text);

    @NotNull List<String> setPlaceholders(OfflinePlayer player, @NotNull List<String> text);

    @NotNull String setPlaceholders(Player player, @NotNull String text);

    @NotNull List<String> setPlaceholders(Player player, @NotNull List<String> text);

    @NotNull String setBracketPlaceholders(OfflinePlayer player, @NotNull String text);

    @NotNull List<String> setBracketPlaceholders(OfflinePlayer player, @NotNull List<String> text);

    String setBracketPlaceholders(Player player, @NotNull String text);

    List<String> setBracketPlaceholders(Player player, @NotNull List<String> text);

    String setRelationalPlaceholders(Player one, Player two, String text);

    List<String> setRelationalPlaceholders(Player one, Player two, List<String> text);

    boolean isRegistered(@NotNull String identifier);

    Set<String> getRegisteredIdentifiers();

    Pattern getPlaceholderPattern();

    Pattern getBracketPlaceholderPattern();

    Pattern getRelationalPlaceholderPattern();

    boolean containsPlaceholders(String text);

    boolean containsBracketPlaceholders(String text);

}
