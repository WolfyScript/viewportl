package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class SpawnParticleEffectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(WolfyUtilities.hasPermission(commandSender, "wolfyutilities.command.spawn_particle_effect")){
                if(args.length >= 1) {
                    String effectName = args[0];
                    NamespacedKey nameSpacedKey;
                    if (effectName.contains(":")) {
                        nameSpacedKey = new NamespacedKey(effectName.split(":")[0], effectName.split(":")[0]);
                    } else {
                        nameSpacedKey = new NamespacedKey("wolfyutilities", effectName);
                    }
                    if (args.length == 2) {
                        ParticleEffects.spawnEffectOnPlayer(nameSpacedKey, EquipmentSlot.HAND, player);
                        player.sendMessage("Spawened particle \"" + nameSpacedKey + "\" on player!");
                    } else {
                        Block block = player.getTargetBlockExact(10);
                        if (block != null) {
                            ParticleEffects.spawnEffectOnBlock(nameSpacedKey, block);
                            player.sendMessage("Spawened particle \"" + nameSpacedKey + "\" on block!");
                        }
                    }
                }
            }
        }
        return false;
    }
}
