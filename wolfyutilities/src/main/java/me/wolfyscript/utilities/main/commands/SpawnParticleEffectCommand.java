package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Location;
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

    public SpawnParticleEffectCommand(WolfyUtilities wolfyUtilities){
        this.wolfyUtilities = wolfyUtilities;
        this.chat = wolfyUtilities.getChat();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (wolfyUtilities.getPermissions().hasPermission(commandSender, "wolfyutilities.command.particle_effect.spawn")) {
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
                                    var location = new Location(player.getWorld(), x, y, z);
                                    var particleEffect = Registry.PARTICLE_EFFECTS.get(nameSpacedKey);
                                    if (particleEffect != null) {
                                        particleEffect.onLocation(location);
                                    }
                                } catch (NumberFormatException ex) {
                                    chat.sendMessage(player, "&cInvalid position! Please make sure you only use numbers for x/y/z!");
                                    return true;
                                }
                            } else {
                                var block = player.getTargetBlockExact(10);
                                if (block != null) {
                                    var particleEffect = Registry.PARTICLE_EFFECTS.get(nameSpacedKey);
                                    if (particleEffect != null) {
                                        particleEffect.onBlock(block);
                                    }
                                }
                            }
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
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("spawn")) {
                        switch (args.length) {
                            case 2:
                                List<String> effects = new ArrayList<>();
                                for (NamespacedKey namespacedKey : Registry.PARTICLE_EFFECTS.keySet()) {
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
