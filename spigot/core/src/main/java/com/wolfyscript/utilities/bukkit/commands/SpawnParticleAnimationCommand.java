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

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleAnimation;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleEffect;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleUtils;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorBasic;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorCircle;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorSphere;
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerLinear;
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerPi;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class SpawnParticleAnimationCommand extends Command implements PluginIdentifiableCommand {

    private final List<String> COMMANDS = Arrays.asList("spawn", "stop");
    private final WolfyCoreCommon core;
    private final BukkitChat chat;

    public SpawnParticleAnimationCommand(WolfyCoreCommon core) {
        super("particle_animation");
        this.core = core;
        this.chat = core.getChat();
        setUsage("/particle_animation spawn");
        setDescription("DEBUG! Spawns a test particle animation on the executor.");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            Audience audience = core.getPlatform().getAudiences().player(player.getUniqueId());
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (core.getWolfyUtils().getPermissions().hasPermission(player, "wolfyutilities.command.particle_animation.spawn")) {
                        var location = player.getLocation();
                        var particleEffect = new ParticleEffect(Particle.FLAME);
                        particleEffect.setTimeSupplier(new TimerPi(40, 2*Math.PI));
                        particleEffect.setAnimator(new AnimatorSphere(2));

                        var first = new ParticleEffect(Particle.SMOKE_NORMAL);
                        first.setTimeSupplier(new TimerLinear(1, 20));
                        first.setAnimator(new AnimatorBasic());

                        var second = new ParticleEffect(Particle.SMOKE_NORMAL);
                        second.setTimeSupplier(new TimerPi(20, 2*Math.PI));
                        second.setAnimator(new AnimatorCircle(1));

                        var animation = new ParticleAnimation(Material.DIAMOND, "", null, 0, 40, 1,
                                new ParticleAnimation.ParticleEffectSettings(first, new Vector(0,0,0), 0),
                                new ParticleAnimation.ParticleEffectSettings(second, new Vector(0,0,0), 5),
                                new ParticleAnimation.ParticleEffectSettings(particleEffect, new Vector(0,0,0), 20)
                        );
                        animation.spawn(location);
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (core.getWolfyUtils().getPermissions().hasPermission(sender, "wolfyutilities.command.particle_effect.spawn")) {
                        if (args.length >= 2) {
                            try {
                                UUID uuid = UUID.fromString(args[1]);
                                ParticleUtils.stopAnimation(uuid);
                                audience.sendMessage(Component.text("Stopped effect with uuid ", NamedTextColor.YELLOW).append(Component.text(args[1], NamedTextColor.GOLD)).append(Component.text("if it was active!")));
                            } catch (IllegalArgumentException ex) {
                                audience.sendMessage(Component.text("Invalid UUID ", NamedTextColor.RED).append(Component.text(args[1], NamedTextColor.DARK_RED)));
                            }
                        }
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
        if (core.getWolfyUtils().getPermissions().hasPermission(sender, "wolfyutilities.command.particle_animation.complete")) {
            if (sender instanceof Player player) {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("spawn")) {
                        switch (args.length) {
                            case 2 -> {
                                List<String> effects = new ArrayList<>();
                                for (NamespacedKey namespacedKey : core.getRegistries().getParticleAnimations().keySet()) {
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
                    } else if (args[0].equalsIgnoreCase("stop")) {
                        ParticleUtils.getActiveAnimations().forEach(uuid -> results.add(uuid.toString()));
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
