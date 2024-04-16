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

package com.wolfyscript.utilities.bukkit.commands;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleEffect;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorSphere;
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerLinear;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public final class SpawnParticleEffectCommand extends Command implements PluginIdentifiableCommand {

    private final List<String> COMMANDS = Arrays.asList("spawn", "stop");
    private final WolfyCoreCommon core;
    private final BukkitChat chat;

    public SpawnParticleEffectCommand(WolfyCoreCommon core) {
        super("particle_effect");
        this.core = core;
        this.chat = core.getChat();
        setUsage("/particle_effect spawn");
        setDescription("DEBUG! Spawns a test particle effect on the target block.");
        setPermission("wolfyutilities.command.particle_effect.spawn");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player && testPermission(player)) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    var block = player.getTargetBlockExact(10);
                    if (block != null) {
                        var particleEffect = new ParticleEffect(Particle.FLAME);
                        particleEffect.setKey(BukkitNamespacedKey.wolfyutilties("test"));
                        particleEffect.setTimeSupplier(new TimerLinear(0.1, 40));
                        particleEffect.setAnimator(new AnimatorSphere(2));
                        particleEffect.spawn(block);
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        List<String> results = new ArrayList<>();
        if (testPermission(sender) && sender instanceof Player player) {
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    switch (args.length) {
                        case 2 -> {
                            List<String> effects = new ArrayList<>();
                            for (NamespacedKey namespacedKey : core.getRegistries().getParticleEffects().keySet()) {
                                effects.add(namespacedKey.toString());
                            }
                            StringUtil.copyPartialMatches(args[1], effects, results);
                        }
                        case 3 -> {
                            results.add("x");
                            results.add(String.valueOf(player.getLocation().getX()));
                        }
                        case 4 -> {
                            results.add("y");
                            results.add(String.valueOf(player.getLocation().getY()));
                        }
                        case 5 -> {
                            results.add("z");
                            results.add(String.valueOf(player.getLocation().getZ()));
                        }
                        default -> {
                            return results;
                        }
                    }
                }
            } else {
                StringUtil.copyPartialMatches(args[0], COMMANDS, results);
            }
        }
        Collections.sort(results);
        return results;
    }
}
