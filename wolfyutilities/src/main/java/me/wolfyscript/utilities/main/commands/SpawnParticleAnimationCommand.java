package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

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
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_animation.spawn")) {
                        if (args.length >= 2) {
                            String effectName = args[1];
                            NamespacedKey nameSpacedKey;
                            if (effectName.contains(":")) {
                                nameSpacedKey = new NamespacedKey(effectName.split(":")[0], effectName.split(":")[1]);
                            } else {
                                nameSpacedKey = new NamespacedKey("wolfyutilities", effectName);
                            }
                            if (args.length >= 5) {
                                try {
                                    double x = Double.parseDouble(args[2]);
                                    double y = Double.parseDouble(args[3]);
                                    double z = Double.parseDouble(args[4]);
                                    Location location = new Location(player.getWorld(), x, y, z);
                                    ParticleUtils.spawnAnimationOnLocation(nameSpacedKey, location);
                                } catch (NumberFormatException ex) {
                                    chat.sendPlayerMessage(player, "&cInvalid position! Please make sure you only use numbers for x/y/z!");
                                    return true;
                                }
                            } else {
                                Block block = player.getTargetBlockExact(10);
                                if (block != null) {
                                    ParticleUtils.spawnAnimationOnBlock(nameSpacedKey, block);
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_effect.spawn")) {
                        if (args.length >= 2) {
                            try {
                                UUID uuid = UUID.fromString(args[1]);
                                ParticleUtils.stopAnimation(uuid);
                                chat.sendPlayerMessage(player, "&eStopped effect with uuid &6" + args[1] + " &eif it was active!");
                            } catch (IllegalArgumentException ex) {
                                chat.sendPlayerMessage(player, "&cInvalid UUID &4" + args[1]);
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
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
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
                        }
                    } else if (args[0].equalsIgnoreCase("stop")) {
                        ParticleUtils.getRunningAnimations().forEach(uuid -> results.add(uuid.toString()));
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
