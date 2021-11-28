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

package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.AnimatorSphere;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpawnParticleEffectCommand implements CommandExecutor, TabCompleter {

    private final List<String> COMMANDS = Arrays.asList("spawn");

    private final WolfyUtilities wolfyUtilities;
    private final Chat chat;

    public SpawnParticleEffectCommand(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.chat = wolfyUtilities.getChat();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_effect.spawn")) {
                        var block = player.getTargetBlockExact(10);
                        if (block != null) {
                            var particleEffect = new ParticleEffect(Particle.FLAME);
                            particleEffect.setKey(NamespacedKey.wolfyutilties("test"));
                            particleEffect.setTimeSupplier(new TimerLinear(0.1, 40));
                            particleEffect.setAnimator(new AnimatorSphere(2));
                            particleEffect.spawn(block);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_effect.complete")) {
            if (commandSender instanceof Player player) {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("spawn")) {
                        switch (args.length) {
                            case 2:
                                List<String> effects = new ArrayList<>();
                                for (NamespacedKey namespacedKey : wolfyUtilities.getRegistries().PARTICLE_EFFECTS.keySet()) {
                                    effects.add(namespacedKey.toString());
                                }
                                StringUtil.copyPartialMatches(args[1], effects, results);
                                break;
                            case 3:
                                results.add("x");
                                results.add(String.valueOf(player.getLocation().getX()));
                                break;
                            case 4:
                                results.add("y");
                                results.add(String.valueOf(player.getLocation().getY()));
                                break;
                            case 5:
                                results.add("z");
                                results.add(String.valueOf(player.getLocation().getZ()));
                                break;
                            default:
                                return results;
                        }
                    }
                } else {
                    StringUtil.copyPartialMatches(args[0], COMMANDS, results);
                }
            }
        }
        Collections.sort(results);
        return results;
    }
}
