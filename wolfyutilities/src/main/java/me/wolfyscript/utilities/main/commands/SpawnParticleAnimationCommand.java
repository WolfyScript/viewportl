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

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import me.wolfyscript.utilities.util.particles.animators.AnimatorBasic;
import me.wolfyscript.utilities.util.particles.animators.AnimatorCircle;
import me.wolfyscript.utilities.util.particles.animators.AnimatorSphere;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import me.wolfyscript.utilities.util.particles.timer.TimerPi;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;

public class SpawnParticleAnimationCommand implements CommandExecutor, TabCompleter {

    private final List<String> COMMANDS = Arrays.asList("spawn", "stop");

    private final WolfyUtilities wolfyUtilities;
    private final Chat chat;

    public SpawnParticleAnimationCommand(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.chat = wolfyUtilities.getChat();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_animation.spawn")) {
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
                    if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_effect.spawn")) {
                        if (args.length >= 2) {
                            try {
                                UUID uuid = UUID.fromString(args[1]);
                                ParticleUtils.stopAnimation(uuid);
                                chat.sendMessage(player, "&eStopped effect with uuid &6" + args[1] + " &eif it was active!");
                            } catch (IllegalArgumentException ex) {
                                chat.sendMessage(player, "&cInvalid UUID &4" + args[1]);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] args) {
        List<String> results = new ArrayList<>();
        if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_animation.complete")) {
            if (commandSender instanceof Player player) {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("spawn")) {
                        switch (args.length) {
                            case 2:
                                List<String> effects = new ArrayList<>();
                                for (NamespacedKey namespacedKey : Registry.PARTICLE_ANIMATIONS.keySet()) {
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
